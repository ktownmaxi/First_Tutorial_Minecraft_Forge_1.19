package net.maxi.tutorialmod.event;

import net.maxi.tutorialmod.TutorialMod;
import net.maxi.tutorialmod.block.entity.ModBlockEntities;
import net.maxi.tutorialmod.block.entity.renderer.GemInfusingStationBlockEntityRenderer;
import net.maxi.tutorialmod.client.ThirstHudOverlay;
import net.maxi.tutorialmod.networking.ModMessages;
import net.maxi.tutorialmod.networking.packet.DrinkWaterC2SPacket;
import net.maxi.tutorialmod.networking.packet.ExampleC2SPacket;
import net.maxi.tutorialmod.util.KeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {
    @Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if(KeyBinding.DRINK_KEY.consumeClick()) {
            ModMessages.sendtoServer(new DrinkWaterC2SPacket());
        }
    } }

@Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModBusEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinding.DRINK_KEY);
    }
    @SubscribeEvent
    public static void registerGuiOverlay(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("thirst", ThirstHudOverlay.HUD_THIRST);
    }
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.GEM_INFUSING_STATION.get(),
                GemInfusingStationBlockEntityRenderer::new);
    }
}
}
