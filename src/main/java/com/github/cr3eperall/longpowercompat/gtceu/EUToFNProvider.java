package com.github.cr3eperall.longpowercompat.gtceu;

import com.github.cr3eperall.longpowercompat.LongUtils;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.compat.CapabilityCompatProvider;
import com.gregtechceu.gtceu.api.capability.compat.EUToFEProvider;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.utils.GTMath;
import com.gregtechceu.gtceu.utils.GTUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.api.FluxCapabilities;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

import javax.annotation.Nonnull;

public class EUToFNProvider extends EUToFEProvider {

    private long feBuffer;

    public EUToFNProvider(BlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
        if (!ConfigHolder.INSTANCE.compat.energy.nativeEUToFE ||
                capability != GTCapability.CAPABILITY_ENERGY_CONTAINER)
            return LazyOptional.empty();

        LazyOptional<IFNEnergyStorage> fnEnergyStorage = getUpvalueCapability(FluxCapabilities.FN_ENERGY_STORAGE, facing);
        if (fnEnergyStorage.isPresent()) {
            return GTCapability.CAPABILITY_ENERGY_CONTAINER.orEmpty(capability,
                    LazyOptional.of(() -> new GTFNEnergyWrapper(fnEnergyStorage.resolve().get())));
        } else {
            LazyOptional<IEnergyStorage> feEnergyStorage = getUpvalueCapability(ForgeCapabilities.ENERGY, facing);
            return feEnergyStorage.isPresent() ?
                    GTCapability.CAPABILITY_ENERGY_CONTAINER.orEmpty(capability,
                            LazyOptional.of(() -> new GTEnergyWrapper(feEnergyStorage.resolve().get()))) :
                    LazyOptional.empty();
        }
    }

    public class GTFNEnergyWrapper implements IEnergyContainer {
        private final IFNEnergyStorage energyStorage;

        public GTFNEnergyWrapper(IFNEnergyStorage energyStorage) {
            this.energyStorage = energyStorage;
        }


        @Override
        public long acceptEnergyFromNetwork(Direction facing, long voltage, long amperage) {
            long receive = 0;

            // Try to use the internal buffer before consuming a new packet
            if (feBuffer > 0) {

                receive = energyStorage.receiveEnergyL(feBuffer, true);

                if (receive == 0)
                    return 0;

                // Internal Buffer could provide the max RF the consumer could consume
                if (feBuffer > receive) {
                    feBuffer -= energyStorage.receiveEnergyL(receive, false);
                    return 0;

                    // Buffer could not provide max value, save the remainder and continue processing
                } else {
                    receive = feBuffer;
                }
            }

            long maxPacket = FeCompat.toFeLong(voltage, FeCompat.ratio(false));
            long maximalValue = maxPacket * amperage;

            // Try to consume our remainder buffer plus a fresh packet
            if (receive != 0) {
                long consumable = energyStorage.receiveEnergyL(LongUtils.saturatedSum(maximalValue, receive), true);

                // Machine unable to consume any power
                if (consumable == 0)
                    return 0;

                consumable = energyStorage.receiveEnergyL(consumable, false);

                // Only able to consume less then our buffered amount
                if (consumable <= receive) {
                    feBuffer = receive - consumable;
                    return 0;
                }

                long newPower = consumable - receive;

                // Able to consume buffered amount plus an even amount of packets (no buffer needed)
                if (newPower % maxPacket == 0) {
                    feBuffer = 0;
                    return newPower / maxPacket;
                }

                // Able to consume buffered amount plus some amount of power with a packet remainder
                long ampsToConsume = (newPower / maxPacket) + 1;
                feBuffer = (maxPacket * ampsToConsume) - newPower;
                return ampsToConsume;

                // Else try to draw 1 full packet
            } else {

                long consumable = energyStorage.receiveEnergyL(maximalValue, true);

                // Machine unable to consume any power
                if (consumable == 0)
                    return 0;

                consumable = energyStorage.receiveEnergyL(consumable, false);

                // Machine unable to actually consume any power
                if (consumable == 0)
                    return 0;

                // Able to consume an even amount of packets
                if (consumable % maxPacket == 0) {
                    feBuffer = 0;
                    return consumable / maxPacket;
                }

                // Able to consume power with some amount of power remainder in the packet
                long ampsToConsume = (consumable / maxPacket) + 1;
                feBuffer = (maxPacket * ampsToConsume) - consumable;
                return ampsToConsume;
            }
        }

        @Override
        public long changeEnergy(long delta) {
            if (delta == 0) return 0;
            else if (delta < 0) {
                int euToFeRatio = FeCompat.ratio(false);
                long extract = energyStorage.extractEnergyL(FeCompat.toFeLong(-delta, euToFeRatio), true);
                return FeCompat.toFeLong(energyStorage.extractEnergyL(extract - extract % euToFeRatio, false), euToFeRatio);
            } else {
                int euToFeRatio = FeCompat.ratio(false);
                long feSent = energyStorage.receiveEnergyL(FeCompat.toFeLong(delta, euToFeRatio), true);
                return FeCompat.toEu(energyStorage.receiveEnergyL(feSent - (feSent % euToFeRatio), false), euToFeRatio);
            }
        }

        @Override
        public long getEnergyCapacity() {
            return FeCompat.toEu(energyStorage.getMaxEnergyStoredL(), FeCompat.ratio(false));
        }

        @Override
        public long getEnergyStored() {
            return FeCompat.toEu(energyStorage.getEnergyStoredL(), FeCompat.ratio(false));
        }

        @Override
        public long getEnergyCanBeInserted() {
            return Math.max(1, getEnergyCapacity()-getEnergyStored());
        }

        @Override
        public long getInputAmperage() {
            return getInputVoltage() == 0 ? 0 : 2;
        }

        @Override
        public long getInputVoltage() {
            long maxInput = energyStorage.receiveEnergyL(Long.MAX_VALUE, true);

            if (maxInput == 0) return 0;
            return GTValues.V[GTUtil
                    .getTierByVoltage(FeCompat.toEu(maxInput, FeCompat.ratio(false)))];
        }

        @Override
        public boolean inputsEnergy(Direction direction) {
            return energyStorage.canReceive();
        }

        @Override
        public boolean outputsEnergy(Direction side) {
            return false;
        }

        @Override
        public boolean isOneProbeHidden() {
            return true;
        }
    }


}
