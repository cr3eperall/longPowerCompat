package com.github.cr3eperall.longpowercompat.mbd2.trait;

import com.github.cr3eperall.longpowercompat.capability.ILongFeStorage;
import com.lowdragmc.mbd2.api.capability.recipe.IO;
import net.minecraftforge.energy.IEnergyStorage;

public class LFeEnergyStorageWrapper implements ILongFeStorage, IEnergyStorage {
    private final CopiableLFeEnergyStorage storage;
    private final IO io;
    private final long maxReceive;
    private final long maxExtract;

    public LFeEnergyStorageWrapper(CopiableLFeEnergyStorage storage, IO io, long maxReceive, long maxExtract) {
        this.storage = storage;
        this.io = io;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (io == IO.IN || io == IO.BOTH){
            return storage.receiveEnergy(Math.min((int)Math.min(this.maxReceive, Integer.MAX_VALUE), maxReceive), simulate);
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (io == IO.OUT || io == IO.BOTH){
            return storage.extractEnergy(Math.min((int)Math.min(this.maxExtract, Integer.MAX_VALUE), maxExtract), simulate);
        }
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return storage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return storage.getMaxEnergyStored();
    }

    @Override
    public long receiveEnergyL(long maxReceive, boolean simulated) {
        if (io == IO.IN || io == IO.BOTH){
            return storage.receiveEnergyL(Math.min(this.maxReceive, maxReceive), simulated);
        }
        return 0;
    }

    @Override
    public long extractEnergyL(long maxExtract, boolean simulated) {
        if (io == IO.OUT || io == IO.BOTH){
            return storage.extractEnergyL(Math.min(this.maxExtract, maxExtract), simulated);
        }
        return 0;
    }

    @Override
    public long getEnergyStoredL() {
        return storage.getEnergyStoredL();
    }

    @Override
    public long getMaxEnergyStoredL() {
        return storage.getMaxEnergyStoredL();
    }

    @Override
    public boolean canExtract() {
        return io == IO.OUT || io == IO.BOTH;
    }

    @Override
    public boolean canReceive() {
        return io == IO.IN || io == IO.BOTH;
    }
}
