package com.github.cr3eperall.longpowercompat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = LongPowerCompat.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue FLUX_NETWORKS_SUPPORT = BUILDER
            .comment("Whether to enable Flux Networks support (requires Flux Networks to be installed)")
            .define("fluxNetworksSupport", true);

//    private static final ForgeConfigSpec.BooleanValue MBD2_SUPPORT = BUILDER
//            .comment("Whether to enable Multiblocked2 support (requires restart and MBD2 to be installed)")
//            .define("mbd2Support", true);

    private static final ForgeConfigSpec.BooleanValue BRANDONSCORE_SUPPORT = BUILDER
            .comment("Whether to enable Brandon's Core(Draconic evolution and related mods) support (requires BrandonsCore to be installed)")
            .define("brandonsCoreSupport", true);

    private static final ForgeConfigSpec.BooleanValue MEKANISM_SUPPORT = BUILDER
            .comment("Whether to enable Mekanism support (requires restart and Mekanism to be installed)")
            .define("mekanismSupport", true);

    private static final ForgeConfigSpec.BooleanValue GREGTECH_SUPPORT = BUILDER
            .comment("Whether to enable GregTech support (requires gtceu to be installed)")
            .define("gregTechSupport", true);

    private static final ForgeConfigSpec.BooleanValue GREGFLUXOLOGY_SUPPORT = BUILDER
            .comment("Whether to enable GregFluxology support (requires Gregfluxology to be installed)")
            .define("gregFluxologySupport", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean fluxNetworksSupport;
    public static boolean mbd2Support = true;
    public static boolean brandonsCoreSupport;
    public static boolean mekanismSupport;
    public static boolean gregTechSupport;
    public static boolean gregFluxologySupport;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        fluxNetworksSupport = FLUX_NETWORKS_SUPPORT.get();
//        mbd2Support = MBD2_SUPPORT.get();
        brandonsCoreSupport = BRANDONSCORE_SUPPORT.get();
        mekanismSupport = MEKANISM_SUPPORT.get();
        gregTechSupport = GREGTECH_SUPPORT.get();
        gregFluxologySupport = GREGFLUXOLOGY_SUPPORT.get();
    }
}
