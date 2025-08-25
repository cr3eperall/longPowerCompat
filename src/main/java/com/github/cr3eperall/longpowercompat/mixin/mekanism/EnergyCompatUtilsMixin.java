package com.github.cr3eperall.longpowercompat.mixin.mekanism;

import com.github.cr3eperall.longpowercompat.mekanism.LFeEnergyCompat;
import mekanism.common.config.listener.ConfigBasedCachedSupplier;
import mekanism.common.config.value.CachedValue;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.integration.energy.IEnergyCompat;
import mekanism.common.integration.energy.StrictEnergyCompat;
import mekanism.common.integration.energy.fluxnetworks.FNEnergyCompat;
import mekanism.common.integration.energy.forgeenergy.ForgeEnergyCompat;
import net.minecraftforge.common.capabilities.Capability;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(EnergyCompatUtils.class)
public class EnergyCompatUtilsMixin {

    @Mutable
    @Shadow
    @Final
    private static List<IEnergyCompat> energyCompats;

    @Inject(
        method = "initLoadedCache",
        at= @At(value = "INVOKE", target = "Lmekanism/common/config/listener/ConfigBasedCachedSupplier;<init>(Lnet/minecraftforge/common/util/NonNullSupplier;[Lmekanism/common/config/value/CachedValue;)V"),
        remap = false
    )
    private static void initLoadedCache(CallbackInfo ci) {
        energyCompats=List.of(
                new StrictEnergyCompat(),
                new FNEnergyCompat(),
                new LFeEnergyCompat(),
                new ForgeEnergyCompat()
        );
    }
}
