package net.maxi.tutorialmod;

import com.mojang.logging.LogUtils;
import net.maxi.tutorialmod.block.ModBlocks;
import net.maxi.tutorialmod.block.entity.ModBlockEntities;
import net.maxi.tutorialmod.entity.ModEntityTypes;
import net.maxi.tutorialmod.entity.client.ChomperRenderer;
import net.maxi.tutorialmod.fluid.ModFluidTypes;
import net.maxi.tutorialmod.fluid.ModFluids;
import net.maxi.tutorialmod.item.ModCreativeModeTab;
import net.maxi.tutorialmod.item.ModItems;
import net.maxi.tutorialmod.loot.ModLootModifiers;
import net.maxi.tutorialmod.networking.ModMessages;
import net.maxi.tutorialmod.painting.ModPaintings;
import net.maxi.tutorialmod.recipe.ModRecipes;
import net.maxi.tutorialmod.screen.GemInfusingStationScreen;
import net.maxi.tutorialmod.screen.ModMenuTypes;
import net.maxi.tutorialmod.villager.ModVillagers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TutorialMod.MOD_ID)
public class TutorialMod
{
    public static final String MOD_ID = "tutorialmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    //very Important Command
    public TutorialMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);

        ModVillagers.register(modEventBus);
        ModPaintings.register(modEventBus);

        ModFluidTypes.register(modEventBus);
        ModFluids.register(modEventBus);

        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        ModRecipes.register(modEventBus);
        ModEntityTypes.register(modEventBus);

        ModLootModifiers.register(modEventBus);

        GeckoLib.initialize();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreativ);
    }
    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {

            ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.JASMINE.getId(), ModBlocks.POTTED_JASMINE);

            ModMessages.register();
            ModVillagers.registerPOIs();
        });
    }

    private void addCreativ(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == ModCreativeModeTab.TUTORIAL_TAB) {
            event.accept(ModItems.ZIRCON);
            event.accept(ModItems.RAW_ZIRCON);
            event.accept(ModItems.EIGHT_BALL);
            event.accept(ModItems.BLUEBERRY);
            event.accept(ModItems.BLUEBERRY_SEEDS);
            event.accept(ModItems.NICE_SWORD);
            event.accept(ModItems.SOAP_WATER_BUCKET);
            event.accept(ModItems.ZIRCON_PICKAXE);
            event.accept(ModItems.CHOMPER_SPAWM_EGG);

            event.accept(ModBlocks.ZIRCON_BLOCK);
            event.accept(ModBlocks.RED_MAPLE_LEAVES);
            event.accept(ModBlocks.RED_MAPLE_PLANKS);
            event.accept(ModBlocks.RED_MAPLE_LOG);
            event.accept(ModBlocks.RED_MAPLE_WOOD);
            event.accept(ModBlocks.RED_MAPLE_SAPLING);
            event.accept(ModBlocks.STRIPPED_RED_MAPLE_LOG);
            event.accept(ModBlocks.STRIPPED_RED_MAPLE_WOOD);
            event.accept(ModBlocks.DEEPSLATE_ZIRCON_ORE);
            event.accept(ModBlocks.ZIRCON_ORE);
            event.accept(ModBlocks.ENDSTONE_ZIRCON_ORE);
            event.accept(ModBlocks.NETHERRACK_ZIRCON_ORE);
            event.accept(ModBlocks.ZIRCON_LAMP);
            event.accept(ModBlocks.JASMINE);
            event.accept(ModBlocks.JUMPY_BLOCK);
            event.accept(ModBlocks.GEM_INFUSING_STATION);
        }
        if (event.getTab() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.ZIRCON);
        }
        if(event.getTab() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModBlocks.ZIRCON_BLOCK);
            event.accept(ModBlocks.RED_MAPLE_LEAVES);
            event.accept(ModBlocks.RED_MAPLE_PLANKS);
            event.accept(ModBlocks.RED_MAPLE_LOG);
            event.accept(ModBlocks.RED_MAPLE_WOOD);
            event.accept(ModBlocks.RED_MAPLE_SAPLING);
            event.accept(ModBlocks.STRIPPED_RED_MAPLE_LOG);
            event.accept(ModBlocks.STRIPPED_RED_MAPLE_WOOD);
            event.accept(ModBlocks.DEEPSLATE_ZIRCON_ORE);
            event.accept(ModBlocks.ZIRCON_ORE);
            event.accept(ModBlocks.ENDSTONE_ZIRCON_ORE);
            event.accept(ModBlocks.NETHERRACK_ZIRCON_ORE);
            event.accept(ModBlocks.ZIRCON_LAMP);
            event.accept(ModBlocks.JASMINE);
            event.accept(ModBlocks.JUMPY_BLOCK);
            event.accept(ModBlocks.GEM_INFUSING_STATION);
        }

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
   @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            ItemBlockRenderTypes.setRenderLayer(ModFluids.SOURCE_SOAP_WATER.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_SOAP_WATER.get(), RenderType.translucent());

            MenuScreens.register(ModMenuTypes.GEM_INFUSING_STATION_MENU.get(), GemInfusingStationScreen::new);

            EntityRenderers.register(ModEntityTypes.CHOMPER.get(), ChomperRenderer::new);
        }
    }
}
