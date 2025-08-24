package com.github.cr3eperall.longpowercompat.mbd2.trait;

import com.lowdragmc.lowdraglib.syncdata.IContentChangeAware;
import com.lowdragmc.lowdraglib.syncdata.ITagSerializable;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.energy.EnergyStorage;
import sonar.fluxnetworks.api.energy.FNEnergyStorage;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

public class CopiableFNEnergyStorage extends FNEnergyStorage implements ITagSerializable<Tag>, IContentChangeAware {
    @Getter
    @Setter
    public Runnable onContentsChanged = () -> {};

    public CopiableFNEnergyStorage(long capacity){
        super(capacity);
    }

    public CopiableFNEnergyStorage(long capacity, long energy){
        super(capacity, capacity, capacity, energy);
    }
    public CopiableFNEnergyStorage(long capacity, long maxReceive, long maxExtract){
        super(capacity, maxReceive, maxExtract);
    }
    public CopiableFNEnergyStorage(long capacity, long maxReceive, long maxExtract, long energy){
        super(capacity, maxReceive, maxExtract, energy);
    }

    @Override
    public long receiveEnergyL(long maxReceive, boolean simulate) {
        long received = super.receiveEnergyL(maxReceive, simulate);
        if(!simulate && received > 0){
            onContentsChanged.run();
        }
        return received;
    }

    @Override
    public long extractEnergyL(long maxExtract, boolean simulate) {
        long extracted = super.extractEnergyL(maxExtract, simulate);
        if(!simulate && extracted > 0){
            onContentsChanged.run();
        }
        return extracted;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received= super.receiveEnergy(maxReceive, simulate);
        if(!simulate && received > 0){
            onContentsChanged.run();
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if(!simulate && extracted > 0){
            onContentsChanged.run();
        }
        return extracted;
    }

    public CopiableFNEnergyStorage copy(){
        return new CopiableFNEnergyStorage(capacity, maxReceive, maxExtract, energy);
    }


    @Override
    public Tag serializeNBT() {
        return LongTag.valueOf(energy);
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (!(nbt instanceof LongTag longNbt))
            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
        this.energy = longNbt.getAsLong();
    }
}
