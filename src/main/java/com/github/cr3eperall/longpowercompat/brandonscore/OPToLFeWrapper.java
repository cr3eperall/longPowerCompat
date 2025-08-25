package com.github.cr3eperall.longpowercompat.brandonscore;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.github.cr3eperall.longpowercompat.capability.ILongFeStorage;

public class OPToLFeWrapper implements ILongFeStorage {
    private final IOPStorage storage;

    public OPToLFeWrapper(IOPStorage storage) {
        this.storage = storage;
    }
    @Override
    public long receiveEnergyL(long maxReceive, boolean simulate) {
        return this.storage.receiveOP(maxReceive, simulate);
    }

    @Override
    public long extractEnergyL(long maxExtract, boolean simulate) {
        return this.storage.extractOP(maxExtract, simulate);
    }

    @Override
    public long getEnergyStoredL() {
        return this.storage.getOPStored();
    }

    @Override
    public long getMaxEnergyStoredL() {
        return this.storage.getMaxOPStored();
    }

    @Override
    public boolean canExtract() {
        return this.storage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return this.storage.canReceive();
    }
}
