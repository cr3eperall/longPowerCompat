package com.github.cr3eperall.longpowercompat.fluxnetworks;

import com.github.cr3eperall.longpowercompat.LongPowerCapabilities;
import com.github.cr3eperall.longpowercompat.capability.ILongFeStorage;
import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sonar.fluxnetworks.api.FluxCapabilities;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

public class LFeToFNCapabilityProvider implements ICapabilityProvider {
    @Getter
    private final ICapabilityProvider upValue;

    public LFeToFNCapabilityProvider(ICapabilityProvider upvalue) {
        this.upValue = upvalue;
    }
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == LongPowerCapabilities.LONG_FE_STORAGE) {
            var upCap = upValue.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side);
            if (upCap.isPresent()) {
                return LongPowerCapabilities.LONG_FE_STORAGE.orEmpty(cap,
                        LazyOptional.of(() -> new FNLongFeEnergyWrapper(upCap.resolve().get()))
                );
            }
        }
        return LazyOptional.empty();
    }

    public class FNLongFeEnergyWrapper implements ILongFeStorage {
        private final IFNEnergyStorage energyStorage;

        public FNLongFeEnergyWrapper(IFNEnergyStorage energyStorage) {
            this.energyStorage = energyStorage;
        }

        @Override
        public long receiveEnergyL(long maxReceive, boolean simulate) {
            return energyStorage.receiveEnergyL(maxReceive, simulate);
        }

        @Override
        public long extractEnergyL(long maxExtract, boolean simulate) {
            return energyStorage.extractEnergyL(maxExtract, simulate);
        }

        @Override
        public long getEnergyStoredL() {
            return energyStorage.getEnergyStoredL();
        }

        @Override
        public long getMaxEnergyStoredL() {
            return energyStorage.getMaxEnergyStoredL();
        }

        @Override
        public boolean canExtract() {
            return energyStorage.canExtract();
        }

        @Override
        public boolean canReceive() {
            return energyStorage.canReceive();
        }
    }

}
