package com.github.cr3eperall.longpowercompat.gtceu;

import com.github.cr3eperall.longpowercompat.LongUtils;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.common.machine.trait.ConverterTrait;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

public class FnContainer extends MachineTrait implements IFNEnergyStorage {
    private final ConverterTrait converter;
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(FnContainer.class);

    public FnContainer(MetaMachine machine, ConverterTrait converter) {
        super(machine);
        this.converter=converter;
    }

    @Override
    public long receiveEnergyL(long maxReceive, boolean simulate) {
        if (converter.isFeToEu() && maxReceive > 0) {
            long received = Math.min(this.getMaxEnergyStoredL() - this.getEnergyStoredL(), maxReceive);
            received -= received % FeCompat.ratio(true);
            if (!simulate) {
                converter.addEnergy(FeCompat.toEu(received, FeCompat.ratio(true)));
            }

            return received;
        } else {
            return 0;
        }
    }

    @Override
    public long extractEnergyL(long maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public long getEnergyStoredL() {
        return LongUtils.saturatedMul(converter.getEnergyStored(), FeCompat.ratio(converter.isFeToEu()));
    }

    @Override
    public long getMaxEnergyStoredL() {
        return LongUtils.saturatedMul(converter.getEnergyCapacity(), FeCompat.ratio(converter.isFeToEu()));
    }

    //        @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return converter.isFeToEu();
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
