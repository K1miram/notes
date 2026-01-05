package kimiram.notes;

import kimiram.notes.item.ModDataComponents;
import kimiram.notes.item.ModItems;
import kimiram.notes.item.component.FinalizedNoteContent;
import kimiram.notes.item.component.NoteContent;
import kimiram.notes.networking.FinalizeNoteC2SPayload;
import kimiram.notes.networking.SaveNoteC2SPayload;
import kimiram.notes.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class Notes implements ModInitializer {
    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(SaveNoteC2SPayload.TYPE, SaveNoteC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(FinalizeNoteC2SPayload.TYPE, FinalizeNoteC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SaveNoteC2SPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.NOTE)) {
                player.setItemInHand(InteractionHand.MAIN_HAND, payload.stack());
            } else if (player.getItemInHand(InteractionHand.OFF_HAND).is(ModItems.NOTE)) {
                player.setItemInHand(InteractionHand.OFF_HAND, payload.stack());
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(FinalizeNoteC2SPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            ItemStack stack = payload.stack();
            NoteContent noteContent = stack.get(ModDataComponents.NOTE_COMPONENT_TYPE);
            stack.remove(ModDataComponents.NOTE_COMPONENT_TYPE);
            stack.remove(DataComponents.MAX_STACK_SIZE);
            ItemStack newStack = new ItemStack(ModItems.FINALIZED_NOTE);
            newStack.applyComponents(stack.getComponents());
            if (noteContent != null) {
                FinalizedNoteContent finalizedNoteContent = new FinalizedNoteContent(Filterable.passThrough(Component.literal(noteContent.text())), noteContent.images());
                newStack.set(ModDataComponents.FINALIZED_NOTE_COMPONENT_TYPE, finalizedNoteContent);
            }
            if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.NOTE)) {
                player.setItemInHand(InteractionHand.MAIN_HAND, newStack);
            } else if (player.getItemInHand(InteractionHand.OFF_HAND).is(ModItems.NOTE)) {
                player.setItemInHand(InteractionHand.OFF_HAND, newStack);
            }
        });

        ModItems.initialize();
        ModDataComponents.initialize();
        ModRecipes.initialize();
    }
}
