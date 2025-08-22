package com.github.cr3eperall.longpowercompat.brandonscore;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.github.cr3eperall.longpowercompat.LongPowerCompat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

public class FNToOPWrapper implements IOPStorage {

    private final IFNEnergyStorage storage;

    private static final Logger LOGGER = LogManager.getLogger(LongPowerCompat.MODID);

    public FNToOPWrapper(IFNEnergyStorage storage) {
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
