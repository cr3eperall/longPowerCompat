package com.github.cr3eperall.longpowercompat.mbd2.trait;

import com.github.cr3eperall.longpowercompat.capability.ILongFeStorage;

import java.util.Arrays;

//TODO: check if overflows are possible
public record LFeEnergyStorageList(ILongFeStorage[] storages) implements ILongFeStorage {
    @Override
    public long receiveEnergyL(long maxReceive, boolean simulated) {
        long received = 0;
        for (ILongFeStorage storage : storages) {
            received += storage.receiveEnergyL(maxReceive - received, simulated);
            if (received >= maxReceive) {
                break;
            }
        }
        return received;
    }

    @Override
    public long extractEnergyL(long maxExtract, boolean simulated) {
        long extracted = 0;
        for (ILongFeStorage storage : storages) {
            extracted += storage.extractEnergyL(maxExtract - extracted, simulated);
            if (extracted >= maxExtract) {
                break;
            }
        }
        return extracted;
    }

    @Override
    public long getEnergyStoredL() {
        return Arrays.stream(storages).reduce(0L, (a, b) -> a + b.getEnergyStoredL(), Long::sum);
    }

    @Override
    public long getMaxEnergyStoredL() {
        return Arrays.stream(storages).reduce(0L, (a, b) -> a + b.getMaxEnergyStoredL(), Long::sum);
    }

    @Override
    public boolean canExtract() {
        return Arrays.stream(storages).anyMatch(ILongFeStorage::canExtract);
    }

    @Override
    public boolean canReceive() {
        return Arrays.stream(storages).anyMatch(ILongFeStorage::canReceive);
    }
}
