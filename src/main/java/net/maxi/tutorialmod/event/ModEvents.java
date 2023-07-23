package net.maxi.tutorialmod.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.maxi.tutorialmod.TutorialMod;
import net.maxi.tutorialmod.block.ModBlocks;
import net.maxi.tutorialmod.item.ModItems;
import net.maxi.tutorialmod.networking.ModMessages;
import net.maxi.tutorialmod.networking.packet.ThirstDatSyncS2CPacket;
import net.maxi.tutorialmod.thirst.PlayerThirst;
import net.maxi.tutorialmod.thirst.PlayerThirstProvider;
import net.maxi.tutorialmod.villager.ModVillagers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if(event.getType() == VillagerProfession.TOOLSMITH) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            ItemStack stack = new ItemStack(ModItems.EIGHT_BALL.get(), 1);
            int villagerLevel = 1;

            trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 2),
                    stack,10,8,0.02F));
        }

        if(event.getType() == ModVillagers.JUMP_MASTER.get()) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
            ItemStack stack = new ItemStack(ModItems.BLUEBERRY.get(), 15);
            int villagerLevel = 1;

            trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 5),
                    stack,10,8,0.02F));

            villagerLevel = 2;
            ItemStack dirtStack = new ItemStack(Blocks.DIRT, 32); // Verkaufe 32 Schmutzblöcke
            trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 1), // Der Preis in Smaragden für den Schmutzblock-Trade
                    dirtStack, // Die 16 Schmutzblöcke, die der Villager im Austausch anbietet
                    10, // Anzahl der maximalen Benutzungen des Trades
                    8, // Erneuerungen pro Level
                    0.02F)); // Preisskalierung

            villagerLevel = 3;
            ItemStack eightball = new ItemStack(ModItems.EIGHT_BALL.get(), 1);
            trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 20),
                    eightball,
                    2,
                    12,
                    0.02F));

            villagerLevel = 3;
            ItemStack zircon_stack = new ItemStack(ModBlocks.ZIRCON_BLOCK.get(), 2);
            trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
                    new ItemStack(Items.EMERALD, 6),
                    zircon_stack,
                    5,
                    4,
                    0.02F));

            ItemStack zircon_lamp= new ItemStack(ModBlocks.ZIRCON_LAMP.get(), 1);
            trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
                    new ItemStack(ModItems.ZIRCON.get(), 2),
                    zircon_lamp,
                    15,
                    5,
                    0.02F));

            villagerLevel = 4;
            ItemStack zircon_to_emerald= new ItemStack(Items.EMERALD, 3);
            trades.get(villagerLevel).add((trader, rand) -> new MerchantOffer(
                    new ItemStack(ModItems.ZIRCON.get(), 1),
                    zircon_to_emerald,
                    12,
                    3,
                    0.02F));

        }

    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof Player) {
            if(!event.getObject().getCapability(PlayerThirstProvider.PLAYER_THIRST).isPresent()) {
                event.addCapability(new ResourceLocation(TutorialMod.MOD_ID, "properties"), new PlayerThirstProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if(event.isWasDeath()) {
            event.getOriginal().getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(oldStore -> {
                event.getOriginal().getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerThirst.class);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.side == LogicalSide.SERVER) {
            event.player.getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(thirst -> {
                if(thirst.getThirst() > 0 && event.player.getRandom().nextFloat() < 0.003f) { // Once Every 10 Seconds on Avg
                    thirst.subThirst(1);
                    ModMessages.sendtoPlayer(new ThirstDatSyncS2CPacket(thirst.getThirst()), (ServerPlayer)event.player);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        if(!event.getLevel().isClientSide()) {
            if(event.getEntity() instanceof ServerPlayer player) {
                player.getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(thirst -> {
                    ModMessages.sendtoPlayer(new ThirstDatSyncS2CPacket(thirst.getThirst()), player);

                });
            }
        }
    }
}