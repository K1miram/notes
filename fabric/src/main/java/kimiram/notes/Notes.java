package kimiram.notes;

import kimiram.notes.item.ModItems;
import kimiram.notes.item.component.FinalizedNoteContent;
import kimiram.notes.item.component.NoteContent;
import kimiram.notes.item.component.NotebookContent;
import kimiram.notes.networking.FinalizeNoteC2SPayload;
import kimiram.notes.networking.FinalizeNotebookC2SPayload;
import kimiram.notes.networking.RemovePageFromNotebookC2SPayload;
import kimiram.notes.networking.SaveNoteC2SPayload;
import kimiram.notes.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static kimiram.notes.Constants.MOD_ID;
import static kimiram.notes.item.ModItems.FINALIZED_NOTEBOOK;
import static kimiram.notes.item.ModItems.NOTEBOOK;
import static kimiram.notes.item.component.ModDataComponents.*;

public class Notes implements ModInitializer {
    public static void registerDataComponents() {
        Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "note_content"),
                NOTE_COMPONENT_TYPE
        );

        Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "finalized_note_content"),
                FINALIZED_NOTE_COMPONENT_TYPE
        );

        Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "notebook_content"),
                NOTEBOOK_COMPONENT_TYPE
        );
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(SaveNoteC2SPayload.TYPE, SaveNoteC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SaveNoteC2SPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            ItemStack stack = payload.stack();
            InteractionHand hand = payload.hand();
            player.setItemInHand(hand, stack);
        });

        PayloadTypeRegistry.playC2S().register(FinalizeNoteC2SPayload.TYPE, FinalizeNoteC2SPayload.CODEC);

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
            player.setItemInHand(hand, newStack);
        });

        PayloadTypeRegistry.playC2S().register(RemovePageFromNotebookC2SPayload.TYPE, RemovePageFromNotebookC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(RemovePageFromNotebookC2SPayload.TYPE, (payload, context) -> {
            ServerPlayer player = context.player();
            int pageIndex = payload.pageIndex();
            InteractionHand hand = payload.hand();
            ItemStack stack = player.getItemInHand(hand);
            NotebookContent content = stack.get(NOTEBOOK_COMPONENT_TYPE);
            List<FinalizedNoteContent> pages = new ArrayList<>();
            if (content != null) {
                pages.addAll(content.pages());
            }
            ItemStack removedPage = ItemStack.EMPTY;
            if (pageIndex >= 0 && pageIndex < pages.size()) {
                removedPage = ModItems.FINALIZED_NOTE.getDefaultInstance();
                removedPage.set(FINALIZED_NOTE_COMPONENT_TYPE, pages.remove(pageIndex));
            }
            if (pages.isEmpty()) {
                stack.remove(NOTEBOOK_COMPONENT_TYPE);
            } else {
                stack.set(NOTEBOOK_COMPONENT_TYPE, new NotebookContent(pages));
            }

            if (!player.addItem(removedPage)) {
                ItemEntity drop = player.drop(removedPage, false);
                if (drop != null) {
                    drop.setNoPickUpDelay();
                    drop.setTarget(player.getUUID());
                }
            }

            player.setItemInHand(hand, stack);
        });

        PayloadTypeRegistry.playC2S().register(FinalizeNotebookC2SPayload.TYPE, FinalizeNotebookC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(FinalizeNotebookC2SPayload.TYPE, ((payload, context) -> {
            ServerPlayer player = context.player();
            InteractionHand hand = payload.hand();
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(NOTEBOOK)) {
                stack = stack.transmuteCopy(FINALIZED_NOTEBOOK);
            }
            player.setItemInHand(hand, stack);
        }));

        ModItems.initialize();
        registerDataComponents();
        ModRecipes.initialize();
    }
}
