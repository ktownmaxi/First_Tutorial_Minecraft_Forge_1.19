package net.maxi.tutorialmod.networking.packet;

import net.maxi.tutorialmod.block.entity.GemInfusingStationBlockEntity;
import net.maxi.tutorialmod.screen.GemInfusingStationMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FluidSyncS2CPacket {
    private final FluidStack fluidStack;
    private final BlockPos pos;
    public FluidSyncS2CPacket(FluidStack fluidStack, BlockPos pos){
        this.fluidStack = fluidStack;
        this.pos = pos;
    }
    public FluidSyncS2CPacket(FriendlyByteBuf buf) {
        this.fluidStack = buf.readFluidStack();
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFluidStack(fluidStack);
        buf.writeBlockPos(pos);
    }


    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            //We are on the Client
            if (Minecraft.getInstance().level.getBlockEntity(pos) instanceof GemInfusingStationBlockEntity blockEntity) {
                blockEntity.setFluid(this.fluidStack);

                if (Minecraft.getInstance().player.containerMenu instanceof GemInfusingStationMenu menu &&
                menu.getBlockEntity().getBlockPos().equals(pos)) {
                    menu.setFluid(this.fluidStack);
                }

            }
        });
    return true;
    }


   private boolean hasWaterAroundThem(ServerPlayer player, ServerLevel level, int size) {

        return level.getBlockStates(player.getBoundingBox().inflate(size))
                .filter(state -> state.is(Blocks.WATER)).toArray().length > 0;
    }
}
