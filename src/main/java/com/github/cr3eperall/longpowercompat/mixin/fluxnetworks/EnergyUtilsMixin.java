package com.github.cr3eperall.longpowercompat.mixin.fluxnetworks;

import com.github.cr3eperall.longpowercompat.fluxnetworks.LFeEnergyConnector;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sonar.fluxnetworks.api.energy.IBlockEnergyConnector;
import sonar.fluxnetworks.api.energy.IItemEnergyConnector;
import sonar.fluxnetworks.common.integration.energy.FNEnergyConnector;
import sonar.fluxnetworks.common.integration.energy.ForgeEnergyConnector;
import sonar.fluxnetworks.common.util.EnergyUtils;

import java.util.ArrayList;
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
     * Reorder the energy connectors to maximize throughput in every case
     */
    @Inject(method = "register", at = @At("TAIL"), remap = false)
    private static void registerTail(CallbackInfo ci) {
        // Empty all the integrations(except the FluxNetworks connector)
        ArrayList<IBlockEnergyConnector> connectors = new ArrayList<>();
        for (IBlockEnergyConnector connector : BLOCK_ENERGY_CONNECTORS) {
            if (!(connector instanceof FNEnergyConnector)) {
                connectors.add(connector);
            }
        }
        BLOCK_ENERGY_CONNECTORS.removeAll(connectors);
        ArrayList<IItemEnergyConnector> itemConnectors = new ArrayList<>();
        for (IItemEnergyConnector connector : ITEM_ENERGY_CONNECTORS) {
            if (!(connector instanceof FNEnergyConnector)) {
                itemConnectors.add(connector);
            }
        }
        ITEM_ENERGY_CONNECTORS.removeAll(itemConnectors);

        // Add our implementation as the first integration
        BLOCK_ENERGY_CONNECTORS.add(LFeEnergyConnector.INSTANCE);
        ITEM_ENERGY_CONNECTORS.add(LFeEnergyConnector.INSTANCE);

        // Re-add all the other integrations, but put the Forge one last
        IBlockEnergyConnector forgeConn=null;
        IItemEnergyConnector forgeItemConn=null;
        for (IBlockEnergyConnector connector : connectors) {
            if (connector instanceof ForgeEnergyConnector) {
                forgeConn = connector;
                continue;
            }
            BLOCK_ENERGY_CONNECTORS.add(connector);
        }
        for (IItemEnergyConnector connector : itemConnectors) {
            if (connector instanceof ForgeEnergyConnector) {
                forgeItemConn = connector;
                continue;
            }
            ITEM_ENERGY_CONNECTORS.add(connector);
        }

        // Finally add the Forge integration
        if (forgeConn != null) BLOCK_ENERGY_CONNECTORS.add(forgeConn);
        if (forgeItemConn != null) ITEM_ENERGY_CONNECTORS.add(forgeItemConn);
    }
}
