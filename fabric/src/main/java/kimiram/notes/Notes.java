package kimiram.notes;

import kimiram.notes.item.ModItems;
import kimiram.notes.item.component.FinalizedNoteContent;
import kimiram.notes.item.component.NoteContent;
import kimiram.notes.networking.FinalizeNoteC2SPayload;
import kimiram.notes.networking.SaveNoteC2SPayload;
import kimiram.notes.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import static kimiram.notes.Constants.MOD_ID;
import static kimiram.notes.item.component.ModDataComponents.FINALIZED_NOTE_COMPONENT_TYPE;
import static kimiram.notes.item.component.ModDataComponents.NOTE_COMPONENT_TYPE;

public class Notes implements ModInitializer {
    public static void registerDataComponents() {
        Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(MOD_ID, "note_content"),
                NOTE_COMPONENT_TYPE
        );

        Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                Identifier.fromNamespaceAndPath(MOD_ID, "finalized_note_content"),
                FINALIZED_NOTE_COMPONENT_TYPE
        );
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.serverboundPlay().register(SaveNoteC2SPayload.TYPE, SaveNoteC2SPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(FinalizeNoteC2SPayload.TYPE, FinalizeNoteC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SaveNoteC2SPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            ItemStack stack = payload.stack();
            InteractionHand hand = payload.hand();
            player.setItemInHand(hand, stack);
        });

        PayloadTypeRegistry.serverboundPlay().register(FinalizeNoteC2SPayload.TYPE, FinalizeNoteC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(FinalizeNoteC2SPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            ItemStack stack = payload.stack();
            InteractionHand hand = payload.hand();
            NoteContent noteContent = stack.get(NOTE_COMPONENT_TYPE);
            stack.remove(NOTE_COMPONENT_TYPE);
            ItemStack newStack = stack.transmuteCopy(ModItems.FINALIZED_NOTE);
            if (noteContent != null) {
                FinalizedNoteContent finalizedNoteContent = new FinalizedNoteContent(Filterable.passThrough(Component.literal(noteContent.text())), noteContent.images());
                newStack.set(FINALIZED_NOTE_COMPONENT_TYPE, finalizedNoteContent);
            }
            if (player.getItemInHand(InteractionHand.MAIN_HAND).is(ModItems.NOTE)) {
                player.setItemInHand(InteractionHand.MAIN_HAND, newStack);
            } else if (player.getItemInHand(InteractionHand.OFF_HAND).is(ModItems.NOTE)) {
                player.setItemInHand(InteractionHand.OFF_HAND, newStack);
            }
            player.setItemInHand(hand, newStack);
        });
        });

        ModItems.initialize();
        registerDataComponents();
        ModRecipes.initialize();
    }
}
