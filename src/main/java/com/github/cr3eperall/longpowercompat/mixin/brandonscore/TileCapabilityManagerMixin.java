package com.github.cr3eperall.longpowercompat.mixin.brandonscore;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.blocks.TileCapabilityManager;
import com.github.cr3eperall.longpowercompat.brandonscore.OPToFNWrapper;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sonar.fluxnetworks.api.FluxCapabilities;

import javax.annotation.Nonnull;

@Mixin(TileCapabilityManager.class)
public abstract class TileCapabilityManagerMixin {
    @Shadow(remap = false)
    public abstract <T> void set(@NotNull Capability<?> cap, @NotNull T capInstance, Direction... sides);

    private static Logger LOGGER = LogManager.getLogger();

    /**
     * This mixin is used to attach the FN Energy Storage capability to TileBCore.
     * This allows increased throughput from FluxNetworks to TileBCore.
     */
    @Inject(method = "set", at= @At("TAIL"), remap = false)
    public <T> void set(@Nonnull Capability<?> cap,@Nonnull T capInstance, Direction[] sides, CallbackInfo ci){
        if (capInstance instanceof IOPStorage storage) {
            OPToFNWrapper fnCap = new OPToFNWrapper(storage);
            this.set(FluxCapabilities.FN_ENERGY_STORAGE, fnCap, sides);
        }
    }
}
