package com.github.cr3eperall.longpowercompat.mekanism;

import com.github.cr3eperall.longpowercompat.Config;
import com.github.cr3eperall.longpowercompat.capability.ILongFeStorage;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.Mekanism;
import mekanism.common.config.MekanismConfig;
import mekanism.common.config.value.CachedValue;
import mekanism.common.integration.energy.IEnergyCompat;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.UnitDisplayUtils;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

public class LFeEnergyCompat implements IEnergyCompat {
    private static final Capability<ILongFeStorage> LFE_ENERGY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    @Override
    public Capability<?> getCapability() {
        return LFE_ENERGY_CAPABILITY;
    }

    @Override
    public boolean isMatchingCapability(Capability<?> capability) {
        return capability == LFE_ENERGY_CAPABILITY;
    }

    @Override
    public boolean isUsable() {
        return Config.mekanismSupport && UnitDisplayUtils.EnergyUnit.FORGE_ENERGY.isEnabled() && !MekanismConfig.general.blacklistForge.get();
    }

    @Override
    public Collection<CachedValue<?>> getBackingConfigs() {
        return Set.of(
                MekanismConfig.general.blacklistForge
        );
    }

    @Override
    public LazyOptional<?> getHandlerAs(IStrictEnergyHandler handler) {
        return LazyOptional.of(() -> new LFeIntegration(handler));
    }

    @Override
    public LazyOptional<IStrictEnergyHandler> getLazyStrictEnergyHandler(ICapabilityProvider provider, @Nullable Direction side) {
        return CapabilityUtils.getCapability(provider, LFE_ENERGY_CAPABILITY, side).lazyMap(LFeStrictEnergyHandler::new);
    }
}
