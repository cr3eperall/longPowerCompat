package com.github.cr3eperall.longpowercompat.fluxnetworks;

import com.github.cr3eperall.longpowercompat.LongPowerCapabilities;
import com.github.cr3eperall.longpowercompat.capability.ILongFeStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import sonar.fluxnetworks.api.energy.IBlockEnergyConnector;
import sonar.fluxnetworks.api.energy.IItemEnergyConnector;
import sonar.fluxnetworks.common.util.FluxUtils;

import javax.annotation.Nonnull;

public class LFeEnergyConnector implements IBlockEnergyConnector, IItemEnergyConnector {

    public static final LFeEnergyConnector INSTANCE = new LFeEnergyConnector();

    @Override
    public boolean hasCapability(@Nonnull BlockEntity target, @Nonnull Direction side) {
        return !target.isRemoved() && target.getCapability(LongPowerCapabilities.LONG_FE_STORAGE, side).isPresent();
    }

    @Override
    public boolean canSendTo(@Nonnull BlockEntity target, @Nonnull Direction side) {
        if (!target.isRemoved()) {
            ILongFeStorage storage = FluxUtils.get(target, LongPowerCapabilities.LONG_FE_STORAGE, side);
            return storage != null && storage.canReceive();
        }
        return false;
    }

    @Override
    public boolean canReceiveFrom(@Nonnull BlockEntity target, @Nonnull Direction side) {
        if (!target.isRemoved()) {
            ILongFeStorage storage = FluxUtils.get(target, LongPowerCapabilities.LONG_FE_STORAGE, side);
            return storage != null && storage.canExtract();
        }
        return false;
    }

    @Override
    public long sendTo(long amount, @Nonnull BlockEntity target, @Nonnull Direction side, boolean simulate) {
        ILongFeStorage storage = FluxUtils.get(target, LongPowerCapabilities.LONG_FE_STORAGE, side);
        return storage == null ? 0 : storage.receiveEnergyL(amount, simulate);
    }

    @Override
    public long receiveFrom(long amount, @Nonnull BlockEntity target, @Nonnull Direction side, boolean simulate) {
        ILongFeStorage storage = FluxUtils.get(target, LongPowerCapabilities.LONG_FE_STORAGE, side);
        return storage == null ? 0 : storage.extractEnergyL(amount, simulate);
    }

    @Override
    public boolean hasCapability(@Nonnull ItemStack stack) {
        return !stack.isEmpty() && stack.getCapability(LongPowerCapabilities.LONG_FE_STORAGE).isPresent();
    }

    @Override
    public boolean canSendTo(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            ILongFeStorage storage = FluxUtils.get(stack, LongPowerCapabilities.LONG_FE_STORAGE);
            return storage != null && storage.canReceive();
        }
        return false;
    }

    @Override
    public boolean canReceiveFrom(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            ILongFeStorage storage = FluxUtils.get(stack, LongPowerCapabilities.LONG_FE_STORAGE);
            return storage != null && storage.canExtract();
        }
        return false;
    }

    @Override
    public long sendTo(long amount, @Nonnull ItemStack stack, boolean simulate) {
        ILongFeStorage storage = FluxUtils.get(stack, LongPowerCapabilities.LONG_FE_STORAGE);
        return storage == null ? 0 : storage.receiveEnergyL(amount, simulate);
    }

    @Override
    public long receiveFrom(long amount, @Nonnull ItemStack stack, boolean simulate) {
        ILongFeStorage storage = FluxUtils.get(stack, LongPowerCapabilities.LONG_FE_STORAGE);
        return storage == null ? 0 : storage.extractEnergyL(amount, simulate);
    }
}
