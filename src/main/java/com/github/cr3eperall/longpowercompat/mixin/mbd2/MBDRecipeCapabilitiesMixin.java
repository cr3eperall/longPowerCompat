package com.github.cr3eperall.longpowercompat.mixin.mbd2;

import com.github.cr3eperall.longpowercompat.mbd2.FluxNetworksRecipeCapability;
import com.lowdragmc.mbd2.api.registry.MBDRegistries;
import com.lowdragmc.mbd2.common.data.MBDRecipeCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MBDRecipeCapabilities.class)
public class MBDRecipeCapabilitiesMixin {
    @Inject(
        method = "init",
        at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/ModLoader;get()Lnet/minecraftforge/fml/ModLoader;"),
        remap = false
    )
    private static void init(CallbackInfo ci) {
        MBDRegistries.RECIPE_CAPABILITIES.register(FluxNetworksRecipeCapability.CAP.name, FluxNetworksRecipeCapability.CAP);
    }
}
