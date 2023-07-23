package net.maxi.tutorialmod.networking.packet;

import net.maxi.tutorialmod.client.ClientThirstData;
import net.maxi.tutorialmod.thirst.PlayerThirstProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ThirstDatSyncS2CPacket {
    private final int thirst;
    public ThirstDatSyncS2CPacket(int thirst){
        this.thirst = thirst;
    }
    public ThirstDatSyncS2CPacket(FriendlyByteBuf buf) {
        this.thirst = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(thirst);
    }


    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            //We are on the Client
            ClientThirstData.setPlayerThirst(thirst);

                });
    return true;
    }


   private boolean hasWaterAroundThem(ServerPlayer player, ServerLevel level, int size) {

        return level.getBlockStates(player.getBoundingBox().inflate(size))
                .filter(state -> state.is(Blocks.WATER)).toArray().length > 0;
    }
}
