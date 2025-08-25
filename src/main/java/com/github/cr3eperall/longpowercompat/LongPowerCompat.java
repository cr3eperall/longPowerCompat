package com.github.cr3eperall.longpowercompat;

import com.github.cr3eperall.longpowercompat.fluxnetworks.LFeToFNCapabilityProvider;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import sonar.fluxnetworks.common.device.TileFluxPlug;
import sonar.fluxnetworks.common.device.TileFluxPoint;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LongPowerCompat.MODID)
@Mod.EventBusSubscriber(
        modid = LongPowerCompat.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class LongPowerCompat
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "longpowercompat";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();


    private void commonSetup(final FMLCommonSetupEvent event)
    {
//        // Some common setup code
//        LOGGER.info("HELLO FROM COMMON SETUP");
//
//        if (Config.logDirtBlock)
//            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
//
//        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);
//
//        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
//        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
//            LOGGER.info("HELLO FROM CLIENT SETUP");
//            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    @SubscribeEvent
    public static void attachTileCapability(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!ModList.get().isLoaded("fluxnetworks")) return;
        if (event.getObject() instanceof TileFluxPlug || event.getObject() instanceof TileFluxPoint){
            event.addCapability(new ResourceLocation("longpowercompat","lfe_to_fn_capability"), new LFeToFNCapabilityProvider(event.getObject()));
        }
    }
}
