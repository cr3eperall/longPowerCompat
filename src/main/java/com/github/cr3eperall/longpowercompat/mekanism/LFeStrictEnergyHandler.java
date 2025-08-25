package com.github.cr3eperall.longpowercompat.mekanism;

import com.github.cr3eperall.longpowercompat.capability.ILongFeStorage;
import mekanism.api.Action;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.util.UnitDisplayUtils;
import org.jetbrains.annotations.NotNull;

public class LFeStrictEnergyHandler implements IStrictEnergyHandler {
    private final ILongFeStorage storage;

    public LFeStrictEnergyHandler(ILongFeStorage storage) {
        this.storage = storage;
    }

    @Override
    public int getEnergyContainerCount() {
        return 1;
    }

    @Override
    public FloatingLong getEnergy(int container) {
        return container == 0 ? UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(storage.getEnergyStoredL()) : FloatingLong.ZERO;
    }

    @Override
    public void setEnergy(int container, FloatingLong energy) {
        //Not implemented or directly needed
    }

    @Override
    public FloatingLong getMaxEnergy(int container) {
        return container == 0 ? UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(storage.getMaxEnergyStoredL()) : FloatingLong.ZERO;
    }

    @Override
    public FloatingLong getNeededEnergy(int container) {
        return container == 0 ? UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(Math.max(0, storage.getMaxEnergyStoredL() - storage.getEnergyStoredL())) : FloatingLong.ZERO;
    }

    @Override
    public FloatingLong insertEnergy(int container, FloatingLong amount, @NotNull Action action) {
        if (container == 0 && storage.canReceive()) {
            long toInsert = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertToAsLong(amount);
            if (toInsert > 0) {
                long inserted = storage.receiveEnergyL(toInsert, action.simulate());
                if (inserted > 0) {
                    //Only bother converting back if any was inserted
                    return amount.subtract(UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(inserted));
                }
            }
        }
        return amount;
    }

    @Override
    public FloatingLong extractEnergy(int container, FloatingLong amount, @NotNull Action action) {
        if (container == 0 && storage.canExtract()) {
            long toExtract = UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertToAsLong(amount);
            if (toExtract > 0) {
                long extracted = storage.extractEnergyL(toExtract, action.simulate());
                return UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.convertFrom(extracted);
            }
        }
        return FloatingLong.ZERO;
    }
}
