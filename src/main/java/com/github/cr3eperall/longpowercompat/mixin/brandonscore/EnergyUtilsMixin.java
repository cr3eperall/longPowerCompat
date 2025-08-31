package com.github.cr3eperall.longpowercompat.mixin.brandonscore;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.brandon3055.brandonscore.capability.OPWrappers;
import com.github.cr3eperall.longpowercompat.Config;
import com.github.cr3eperall.longpowercompat.LongPowerCapabilities;
import com.github.cr3eperall.longpowercompat.brandonscore.LFeToOPWrapper;
import com.github.cr3eperall.longpowercompat.capability.ILongFeStorage;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(com.brandon3055.brandonscore.utils.EnergyUtils.class)
public class EnergyUtilsMixin {

    /**
     * @author cr3eperall
     * @reason Add a check and wrapper for FluxNetworks energy
     * This method is overwritten to allow increased throughput from TileBCore to FluxNetworks
     * TODO: use an Inject for better compatibility
     */
    @Overwrite(remap = false)
    public static IOPStorage getStorageFromProvider(ICapabilityProvider provider, Direction side) {
        LazyOptional<IOPStorage> op = provider.getCapability(CapabilityOP.OP, side);
        if (op.isPresent()) {
            return op.orElseThrow(NullPointerException::new); // not throwing a ImpossibleException here because I can't get it to work.
        }
        if(Config.brandonsCoreSupport) {
            LazyOptional<ILongFeStorage> lFe = provider.getCapability(LongPowerCapabilities.LONG_FE_STORAGE, side);
            if (lFe.isPresent()) {
                return new LFeToOPWrapper(lFe.orElseThrow(NullPointerException::new)); // not throwing a ImpossibleException here because I can't get it to work.
            }
        }
        LazyOptional<IEnergyStorage> fe = provider.getCapability(ForgeCapabilities.ENERGY, side);
        if (fe.isPresent()) {
            return new OPWrappers.FE(fe.orElseThrow(NullPointerException::new)); // not throwing a ImpossibleException here because I can't get it to work.
        }
        return null;
    }

}
