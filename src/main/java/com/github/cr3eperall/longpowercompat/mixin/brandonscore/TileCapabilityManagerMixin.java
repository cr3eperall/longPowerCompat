package com.github.cr3eperall.longpowercompat.mixin.brandonscore;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.blocks.TileCapabilityManager;
import com.github.cr3eperall.longpowercompat.LongPowerCapabilities;
import com.github.cr3eperall.longpowercompat.brandonscore.OPToLFeWrapper;
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

import javax.annotation.Nonnull;

@Mixin(TileCapabilityManager.class)
public abstract class TileCapabilityManagerMixin {
    @Shadow(remap = false)
    public abstract <T> void set(@NotNull Capability<?> cap, @NotNull T capInstance, Direction... sides);

    private static Logger LOGGER = LogManager.getLogger();

    /**
     * This mixin is used to attach the Long FE Energy Storage capability to TileBCore.
     * This allows increased throughput from Long FE to TileBCore.
     */
    @Inject(method = "set", at= @At("TAIL"), remap = false)
    public <T> void set(@Nonnull Capability<?> cap,@Nonnull T capInstance, Direction[] sides, CallbackInfo ci){
        if (capInstance instanceof IOPStorage storage) {
            OPToLFeWrapper lFeCap = new OPToLFeWrapper(storage);
            this.set(LongPowerCapabilities.LONG_FE_STORAGE, lFeCap, sides);
        }
    }
}
