package net.maxi.tutorialmod.networking.packet;

import net.maxi.tutorialmod.thirst.PlayerThirst;
import net.maxi.tutorialmod.thirst.PlayerThirstProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;
import net.maxi.tutorialmod.thirst.PlayerThirst.*;

import java.util.function.Supplier;

public class DrinkWaterC2SPacket {
    private static final String MESSAGE_DRINK_WATER = "message.tutorialmod.dink_water";
    private static final String MESSAGE_NO_WATER = "message.tutorialmod.no_water";
    public DrinkWaterC2SPacket(){

    }
    public DrinkWaterC2SPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }


    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            //HERE IS THE SERVER SIDE
            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();

            if (hasWaterAroundThem(player, level, 2)) {
                //Notify player the player
                player.sendSystemMessage(Component.translatable(MESSAGE_DRINK_WATER).withStyle(ChatFormatting.AQUA));
                //play drinking sound
                level.playSound(null, player.getOnPos(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS,
                        0.5F, level.random.nextFloat() * 0.1F +0.9F);
                //increase water level
                player.getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(thirst -> {
                    thirst.addThirst(1);
                    player.sendSystemMessage(Component.literal("Current Thirst " + thirst.getThirst())
                            .withStyle(ChatFormatting.AQUA));
                });


            } else {
                //Notify the player
                player.sendSystemMessage(Component.translatable(MESSAGE_NO_WATER).withStyle(ChatFormatting.RED));
                // Output the current thirst level
                player.getCapability(PlayerThirstProvider.PLAYER_THIRST).ifPresent(thirst -> {

                    player.sendSystemMessage(Component.literal("Current Thirst " + thirst.getThirst())
                            .withStyle(ChatFormatting.AQUA));
                });


            }



        });
        return true;
    }

    private boolean hasWaterAroundThem(ServerPlayer player, ServerLevel level, int size) {

        return level.getBlockStates(player.getBoundingBox().inflate(size))
                .filter(state -> state.is(Blocks.WATER)).toArray().length > 0;
    }
}
