package com.github.cr3eperall.longpowercompat.brandonscore;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.github.cr3eperall.longpowercompat.LongPowerCompat;
import com.github.cr3eperall.longpowercompat.capability.ILongFeStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LFeToOPWrapper implements IOPStorage {

    private final ILongFeStorage storage;

    private static final Logger LOGGER = LogManager.getLogger(LongPowerCompat.MODID);

    public LFeToOPWrapper(ILongFeStorage storage) {
        this.storage = storage;
    }

    @Override
    public long receiveOP(long maxReceive, boolean simulate) {
        return storage.receiveEnergyL(maxReceive, simulate);
    }

    @Override
    public long extractOP(long maxExtract, boolean simulate) {
        return storage.receiveEnergyL(maxExtract, simulate);
    }

    @Override
    public long getOPStored() {
        return storage.getEnergyStoredL();
    }

    @Override
    public long getMaxOPStored() {
        return storage.getMaxEnergyStoredL();
    }

    @Override
    public boolean canExtract() {
        return storage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return storage.canReceive();
    }

    /**
     * ModifyEnergyStored is not supported by FluxNetworks
     */
    @Override
    public long modifyEnergyStored(long amount) {
        return 0;
    }
}
