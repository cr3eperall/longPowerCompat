package com.github.cr3eperall.longpowercompat.mixin.gtceu;

import com.github.cr3eperall.longpowercompat.LongUtils;
import com.github.cr3eperall.longpowercompat.gtceu.FnContainer;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.common.machine.electric.ConverterMachine;
import com.gregtechceu.gtceu.common.machine.trait.ConverterTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import sonar.fluxnetworks.api.FluxCapabilities;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

@Mixin(value = ConverterTrait.class)
public abstract class ConverterTraitMixin extends NotifiableEnergyContainer {
    @Shadow
    @Final
    private long voltage;
    private FnContainer fnContainer;

    public ConverterTraitMixin(MetaMachine machine, long maxCapacity, long maxInputVoltage, long maxInputAmperage, long maxOutputVoltage, long maxOutputAmperage) {
        super(machine, maxCapacity, maxInputVoltage, maxInputAmperage, maxOutputVoltage, maxOutputAmperage);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(ConverterMachine machine, int amps, CallbackInfo ci) {
        this.fnContainer=new FnContainer(machine, (ConverterTrait)(Object)this);
    }

    @Inject(method = "serverTick",
            at = @At(value = "INVOKE", target = "Lcom/gregtechceu/gtceu/api/capability/GTCapabilityHelper;getForgeEnergy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Lnet/minecraftforge/energy/IEnergyStorage;"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true,
            remap = false
    )
    public void serverTick(CallbackInfo ci, Direction frontFacing) {
        @SuppressWarnings("ReassignedVariable")
        IFNEnergyStorage fnEnergyContainer=null;
        Level level = this.machine.getLevel();
        BlockPos pos = this.machine.getPos().relative(frontFacing);
        Direction side = frontFacing.getOpposite();

        if (level.getBlockState(pos).hasBlockEntity()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                fnEnergyContainer=(IFNEnergyStorage)blockEntity.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side).orElse(null);
            }
        }

        if (fnEnergyContainer != null && fnEnergyContainer.canReceive()) {
            int euToFeRatio = FeCompat.ratio(false);
            long amountEU= Math.min(this.getEnergyStored(), this.voltage * this.amps);
            long feSent = fnEnergyContainer.receiveEnergyL(FeCompat.toFeLong(amountEU, euToFeRatio), true);
            long energyUsed =FeCompat.toEu(fnEnergyContainer.receiveEnergyL(feSent - feSent % euToFeRatio, false), euToFeRatio);
            if (energyUsed > 0L) {
                this.setEnergyStored(this.getEnergyStored() - energyUsed);
                // if we sent any energy we can skip checking for ForgeEnergy
                ci.cancel();
            }
        }
    }
}
