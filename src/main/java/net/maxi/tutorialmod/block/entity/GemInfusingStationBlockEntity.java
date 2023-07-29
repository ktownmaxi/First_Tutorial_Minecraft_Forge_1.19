package net.maxi.tutorialmod.block.entity;

import net.maxi.tutorialmod.item.ModItems;
import net.maxi.tutorialmod.screen.GemInfusingStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.plaf.basic.BasicComboBoxUI;

public class GemInfusingStationBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler ItemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;


    public GemInfusingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GEM_INFUSING_STATION.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> GemInfusingStationBlockEntity.this.progress;
                    case  1 -> GemInfusingStationBlockEntity.this.maxProgress;
                    default ->  0;
                };
            }

            @Override
            public void set(int p_39285_, int p_39286_) {

            }

            @Override
            public int getCount() {
                return 0;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Gem Infusing Station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new GemInfusingStationMenu(id, inventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {  // Change not sure if working
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> ItemHandler);
    }

    @Override
    public void invalidateCaps() {
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", ItemHandler.serializeNBT());
        nbt.putInt("gem_infusing_station_progress", this.progress);

        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        ItemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("gem_infusing_station_progress");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(ItemHandler.getSlots());
        for (int i = 0; i < ItemHandler.getSlots(); i++) {
            inventory.setItem(i, ItemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GemInfusingStationBlockEntity pEntity) {
        if(level.isClientSide()) {
            return;
        }

        if(hasRecipe(pEntity)) {
            pEntity.progress++;
            setChanged(level, pos, state);

            if(pEntity.progress >= pEntity.maxProgress) {
                craftItem(pEntity);
            }
        } else {
            pEntity.resetProgress();
            setChanged(level, pos, state);
        }
    }

    private void resetProgress() {
        this.progress = 0;
        }

    private static void craftItem(GemInfusingStationBlockEntity pEntity){
        if (hasRecipe(pEntity)) {
            pEntity.ItemHandler.extractItem(1, 1, false);
            pEntity.ItemHandler.setStackInSlot(2, new ItemStack(ModItems.ZIRCON.get(),
                    pEntity.ItemHandler.getStackInSlot(2).getCount() + 1 ));

            pEntity.resetProgress();

        }
    }

    private static boolean hasRecipe(GemInfusingStationBlockEntity entity){
        SimpleContainer inventory = new SimpleContainer(entity.ItemHandler.getSlots());
        for (int i = 0; i < entity.ItemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.ItemHandler.getStackInSlot(i));
        }

        boolean hasRawGemInFirstSlot = entity.ItemHandler.getStackInSlot(1).getItem() == ModItems.RAW_ZIRCON.get();

        return hasRawGemInFirstSlot && canInsertAmountIntoOutputSlot(inventory) &&
                canInsertItemIntoOutputSlot(inventory, new ItemStack(ModItems.ZIRCON.get(), 1));
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack itemStack) {

        return inventory.getItem(2).getMaxStackSize() > inventory.getItem(2).getCount();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {

        return inventory.getItem(2).getMaxStackSize() > inventory.getItem(2).getCount();
    }


}
