package com.github.cr3eperall.longpowercompat.mixin.fluxnetworks;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sonar.fluxnetworks.api.energy.IBlockEnergyConnector;
import sonar.fluxnetworks.api.energy.IItemEnergyConnector;
import sonar.fluxnetworks.common.integration.energy.ForgeEnergyConnector;
import sonar.fluxnetworks.common.util.EnergyUtils;

import java.util.List;

@Mixin(EnergyUtils.class)
public final class EnergyUtilsMixin {

    @Shadow
    @Final
    private static List<IBlockEnergyConnector> BLOCK_ENERGY_CONNECTORS;

    @Shadow
    @Final
    private static List<IItemEnergyConnector> ITEM_ENERGY_CONNECTORS;

    /**
     * Reorder the energy connectors to check everything before defaulting to ForgeEnergyConnector.
     */
    @Inject(method = "register", at = @At("TAIL"), remap=false)
    private static void register(CallbackInfo ci){
        IBlockEnergyConnector forgeBlockEnergyConnector=null;
        for(IBlockEnergyConnector connector : BLOCK_ENERGY_CONNECTORS){
            if (connector instanceof ForgeEnergyConnector){
                forgeBlockEnergyConnector=connector;
                BLOCK_ENERGY_CONNECTORS.remove(connector);
            }
        }
        IItemEnergyConnector forgeItemEnergyConnector=null;
        for(IItemEnergyConnector connector : ITEM_ENERGY_CONNECTORS){
            if (connector instanceof ForgeEnergyConnector){
                forgeItemEnergyConnector=connector;
                ITEM_ENERGY_CONNECTORS.remove(connector);
            }
        }
        BLOCK_ENERGY_CONNECTORS.add(forgeBlockEnergyConnector);
        ITEM_ENERGY_CONNECTORS.add(forgeItemEnergyConnector);
    }
}
