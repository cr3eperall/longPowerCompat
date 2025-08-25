package com.github.cr3eperall.longpowercompat.mixin.gtceu;

import com.github.cr3eperall.longpowercompat.LongPowerCapabilities;
import com.github.cr3eperall.longpowercompat.capability.ILongFeStorage;
import com.github.cr3eperall.longpowercompat.gtceu.LFeContainer;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.common.machine.electric.ConverterMachine;
import com.gregtechceu.gtceu.common.machine.trait.ConverterTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ConverterTrait.class)
public abstract class ConverterTraitMixin extends NotifiableEnergyContainer {
    @Shadow
    @Final
    private long voltage;
    @Shadow
    @Final
    private int amps;
    private LFeContainer lFeContainer;

    public ConverterTraitMixin(MetaMachine machine, long maxCapacity, long maxInputVoltage, long maxInputAmperage, long maxOutputVoltage, long maxOutputAmperage) {
        super(machine, maxCapacity, maxInputVoltage, maxInputAmperage, maxOutputVoltage, maxOutputAmperage);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(ConverterMachine machine, int amps, CallbackInfo ci) {
        this.lFeContainer=new LFeContainer(machine, (ConverterTrait)(Object)this);
    }

    @Inject(method = "serverTick",
            at = @At(value = "INVOKE", target = "Lcom/gregtechceu/gtceu/api/capability/GTCapabilityHelper;getForgeEnergy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Lnet/minecraftforge/energy/IEnergyStorage;"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true,
            remap = false
    )
    public void serverTick(CallbackInfo ci, Direction frontFacing) {
        @SuppressWarnings("ReassignedVariable")
        ILongFeStorage lFeEnergyContainer=null;
        Level level = this.machine.getLevel();
        BlockPos pos = this.machine.getPos().relative(frontFacing);
        Direction side = frontFacing.getOpposite();

        if (level.getBlockState(pos).hasBlockEntity()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                lFeEnergyContainer=(ILongFeStorage)blockEntity.getCapability(LongPowerCapabilities.LONG_FE_STORAGE, side).orElse(null);
            }
        }

        if (lFeEnergyContainer != null && lFeEnergyContainer.canReceive()) {
            int euToFeRatio = FeCompat.ratio(false);
            long amountEU= Math.min(this.getEnergyStored(), this.voltage * this.amps);
            long feSent = lFeEnergyContainer.receiveEnergyL(FeCompat.toFeLong(amountEU, euToFeRatio), true);
            long energyUsed =FeCompat.toEu(lFeEnergyContainer.receiveEnergyL(feSent - feSent % euToFeRatio, false), euToFeRatio);
            if (energyUsed > 0L) {
                this.setEnergyStored(this.getEnergyStored() - energyUsed);
                // if we sent any energy we can skip checking for ForgeEnergy
                ci.cancel();
            }
        }
    }
}
