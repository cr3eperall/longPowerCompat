package com.github.cr3eperall.longpowercompat.mixin.gtceu;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sonar.fluxnetworks.api.FluxCapabilities;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

import java.util.List;

@Mixin(value = MetaMachineBlockEntity.class, remap = false)
public abstract class MetaMachineBlockEntityMixin {
    @Shadow
    public static <T> List<T> getCapabilitiesFromTraits(List<MachineTrait> traits, Direction accessSide, Class<T> capability) {
        return null;
    }

    @Inject(
            method = "getCapability(Lcom/gregtechceu/gtceu/api/machine/MetaMachine;Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/core/Direction;)Lnet/minecraftforge/common/util/LazyOptional;",
            at = @At(value = "INVOKE", target = "Lcom/gregtechceu/gtceu/GTCEu$Mods;isAE2Loaded()Z"),
            cancellable = true)
    private static <T> void getCapability(MetaMachine machine, @NotNull Capability<T> cap, @Nullable Direction side, CallbackInfoReturnable<LazyOptional<T>> cir) {
        if (machine instanceof IFNEnergyStorage energyStorage) {
            cir.setReturnValue(
                    FluxCapabilities.FN_ENERGY_STORAGE.orEmpty(cap, LazyOptional.of(() -> energyStorage))
            );
        }

        List<IFNEnergyStorage> list = getCapabilitiesFromTraits(machine.getTraits(), side, IFNEnergyStorage.class);
        if (!list.isEmpty()) {
            cir.setReturnValue(
                    FluxCapabilities.FN_ENERGY_STORAGE.orEmpty(cap, LazyOptional.of(() -> (IFNEnergyStorage) list.get(0)))
            );
        }
    }
}
