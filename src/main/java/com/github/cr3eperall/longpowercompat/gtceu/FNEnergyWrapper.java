package com.github.cr3eperall.longpowercompat.gtceu;

import com.github.cr3eperall.longpowercompat.LongUtils;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import com.gregtechceu.gtceu.common.pipelike.cable.EnergyNetHandler;
import gregfluxology.GFyConfig;
import gregfluxology.util.GFyUtility;
import net.minecraft.core.Direction;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

public class FNEnergyWrapper implements IFNEnergyStorage{
    private final IEnergyContainer energyContainer;
    private final Direction facing;

    public FNEnergyWrapper(IEnergyContainer energyContainer, Direction facing) {
        this.energyContainer = energyContainer;
        this.facing = facing;
    }

    @Override
    public long receiveEnergyL(long maxReceive, boolean simulate) {
        if (!this.canReceive()) {
            return 0;
        } else if (maxReceive == 1 && simulate) {
            return this.energyContainer.getEnergyCanBeInserted() > 0L ? 1 : 0;
        } else {
            long maxIn = (long)(maxReceive / FeCompat.ratio(true));
            long missing = this.energyContainer.getEnergyCanBeInserted();
            long voltage = this.energyContainer.getInputVoltage();
            if (voltage <=0) {
                // This may happen if it's the creative energy source
                return 0;
            }
            maxIn = Math.min(missing, maxIn);
            long maxAmp = Math.min(this.energyContainer.getInputAmperage(), maxIn / voltage);
            if (GFyConfig.ignoreCableCapacity && this.energyContainer instanceof EnergyNetHandler) {
                maxIn = (long)(maxReceive / FeCompat.ratio(true));
                maxAmp = maxIn / voltage;
            }

            if (maxAmp < 1L) {
                return 0;
            } else {
                if (!simulate) {
                    maxAmp = this.energyContainer.acceptEnergyFromNetwork(this.facing, voltage, maxAmp);
                }

                long received = LongUtils.saturatedMul(LongUtils.saturatedMul(maxAmp, voltage), FeCompat.ratio(false));

                return received;
            }
        }
    }

    @Override
    public long extractEnergyL(long maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public long getEnergyStoredL() {
        return LongUtils.saturatedMul(this.energyContainer.getEnergyStored(), FeCompat.ratio(false));
    }

    @Override
    public long getMaxEnergyStoredL() {
        return LongUtils.saturatedMul(this.energyContainer.getEnergyCapacity(), FeCompat.ratio(false));
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return this.energyContainer.inputsEnergy(this.facing);
    }
}
