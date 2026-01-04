package kimiram.notes;

import kimiram.notes.item.ModItems;
import kimiram.notes.networking.FinalizeNoteC2SPacket;
import kimiram.notes.networking.SaveNoteC2SPacket;
import kimiram.notes.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import static kimiram.notes.item.ModItems.FINALIZED_NOTE;
import static kimiram.notes.item.ModItems.NOTE;

public class Notes implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(SaveNoteC2SPacket.TYPE, (payload, player, sender) -> {
            if (player.getItemInHand(InteractionHand.MAIN_HAND).is(NOTE)) {
                player.setItemInHand(InteractionHand.MAIN_HAND, payload.stack());
            } else if (player.getItemInHand(InteractionHand.OFF_HAND).is(NOTE)) {
                player.setItemInHand(InteractionHand.OFF_HAND, payload.stack());
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(FinalizeNoteC2SPacket.TYPE, (payload, player, sender) -> {
            ItemStack stack = new ItemStack(FINALIZED_NOTE);
            if (payload.stack().getTag() != null) {
                CompoundTag tag = payload.stack().getTag().copy();
                tag.put("finalized_note_content", tag.getCompound("note_content"));
                tag.remove("note_content");
                stack.setTag(tag);
            }
            if (player.getItemInHand(InteractionHand.MAIN_HAND).is(NOTE)) {
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            } else if (player.getItemInHand(InteractionHand.OFF_HAND).is(NOTE)) {
                player.setItemInHand(InteractionHand.OFF_HAND, stack);
            }
        });

        ModItems.initialize();
        ModRecipes.initialize();
    }
}
