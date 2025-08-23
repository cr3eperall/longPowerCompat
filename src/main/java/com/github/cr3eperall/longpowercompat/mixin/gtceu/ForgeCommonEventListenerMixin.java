package com.github.cr3eperall.longpowercompat.mixin.gtceu;

import com.github.cr3eperall.longpowercompat.gtceu.EUToFNProvider;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.forge.ForgeCommonEventListener;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        value = ForgeCommonEventListener.class,
        remap = false,
        priority = 1001 // Make sure this runs after GregFluxology (default priority 1000)
)
public class ForgeCommonEventListenerMixin {
    @Inject(
            method = {"registerBlockEntityCapabilities"},
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/AttachCapabilitiesEvent;addCapability(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraftforge/common/capabilities/ICapabilityProvider;)V"),
            cancellable = true
    )
    private static void attachTileCapability(AttachCapabilitiesEvent<BlockEntity> event, CallbackInfo ci) {
        event.addCapability(GTCEu.id("fn_capability"), new EUToFNProvider(event.getObject()));
        ci.cancel();
    }
}
