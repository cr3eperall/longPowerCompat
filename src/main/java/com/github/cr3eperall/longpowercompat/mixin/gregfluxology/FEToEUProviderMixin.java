package com.github.cr3eperall.longpowercompat.mixin.gregfluxology;

import com.github.cr3eperall.longpowercompat.Config;
import com.github.cr3eperall.longpowercompat.LongPowerCapabilities;
import com.github.cr3eperall.longpowercompat.gtceu.LFeEnergyWrapper;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.compat.CapabilityCompatProvider;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import gregfluxology.cap.FEToEUProvider;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(FEToEUProvider.class)
public abstract class FEToEUProviderMixin extends CapabilityCompatProvider {

    public FEToEUProviderMixin(ICapabilityProvider upvalue) {
        super(upvalue);
    }

    @Inject(method = "getCapability", at = @At("HEAD"), remap = false, cancellable = true)
    public <T> void getCapability(@NotNull Capability<T> capability, Direction facing, CallbackInfoReturnable<LazyOptional<T>> cir) {
        if (Config.gregFluxologySupport && capability == LongPowerCapabilities.LONG_FE_STORAGE) {
            LazyOptional<IEnergyContainer> energyContainer = this.getUpvalueCapability(GTCapability.CAPABILITY_ENERGY_CONTAINER, facing);
            cir.setReturnValue(energyContainer.isPresent() ?
                    LongPowerCapabilities.LONG_FE_STORAGE.orEmpty(capability,
                        LazyOptional.of(() -> new LFeEnergyWrapper(energyContainer.resolve().get(), facing))) :
                    LazyOptional.empty());
        }
    }

    @Mixin(targets = "gregfluxology.cap.FEToEUProvider$FEEnergyWrapper")
    private abstract static class FEEnergyWrapper implements IEnergyStorage{
        /**
         * fix divided by zero crash
         * TODO: remove when gregfluxology is fixed
         */
        @Inject(method = "receiveEnergy",
                at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(JJ)J", ordinal = 0),
                locals = LocalCapture.CAPTURE_FAILHARD,
                cancellable = true,
                remap = false
        )
        public void receiveEnergy(int maxReceive, boolean simulate, CallbackInfoReturnable<Integer> cir, long maxIn, long missing, long voltage) {
            if (voltage <=0) {
                // This may happen if it's the creative energy source
                cir.setReturnValue(0);
            }
        }
    }
}
