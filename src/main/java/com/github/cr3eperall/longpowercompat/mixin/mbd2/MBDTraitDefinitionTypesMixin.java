package com.github.cr3eperall.longpowercompat.mixin.mbd2;

import com.github.cr3eperall.longpowercompat.mbd2.trait.LongFeEnergyCapabilityTraitDefinition;
import com.lowdragmc.mbd2.common.data.MBDTraitDefinitionTypes;
import com.lowdragmc.mbd2.common.trait.TraitDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MBDTraitDefinitionTypes.class)
public abstract class MBDTraitDefinitionTypesMixin {

    @Shadow
    public static void register(Class<? extends TraitDefinition> clazz) {
    }

    @Inject(
        method = "init",
        at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/ModLoader;get()Lnet/minecraftforge/fml/ModLoader;"),
        remap=false
    )
    private static void init(CallbackInfo ci) {
        register(LongFeEnergyCapabilityTraitDefinition.class);
    }

}
