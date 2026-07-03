package kimiram.notes.client;

import kimiram.notes.client.gui.screen.FinalizedNoteScreen;
import kimiram.notes.client.gui.screen.NoteScreen;
import kimiram.notes.client.gui.screen.NotebookScreen;
import kimiram.notes.networking.FinalizeNoteC2SPayload;
import kimiram.notes.networking.FinalizeNotebookC2SPayload;
import kimiram.notes.networking.RemovePageFromNotebookC2SPayload;
import kimiram.notes.networking.SaveNoteC2SPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class NotesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

    }

    public static void openNote(ItemStack stack, InteractionHand hand) {
        Minecraft.getInstance().gui.setScreen(new NoteScreen(stack, newStack -> saveNote(newStack, hand), newStack -> finalizeNote(newStack, hand)));
    }

    private static void saveNote(ItemStack stack, InteractionHand hand) {
        ClientPlayNetworking.send(new SaveNoteC2SPayload(stack, hand));
    }

    private static void finalizeNote(ItemStack stack, InteractionHand hand) {
        ClientPlayNetworking.send(new FinalizeNoteC2SPayload(stack, hand));
    }

    public static void openFinalizedNote(ItemStack stack) {
        Minecraft.getInstance().gui.setScreen(new FinalizedNoteScreen(stack));
    }

    public static void openNotebook(ItemStack stack, InteractionHand hand, Identifier id) {
        Minecraft.getInstance().gui.setScreen(new NotebookScreen(
                stack,
                id,
                pageIndex -> removePage(hand, pageIndex),
                () -> finalizeNotebook(hand))
        );
    }

    private static void removePage(InteractionHand hand, int pageIndex) {
        ClientPlayNetworking.send(new RemovePageFromNotebookC2SPayload(hand, pageIndex));
    }

    private static void finalizeNotebook(InteractionHand hand) {
        ClientPlayNetworking.send(new FinalizeNotebookC2SPayload(hand));
    }

    public static void openFinalizedNotebook(ItemStack stack, Identifier id) {
        Minecraft.getInstance().gui.setScreen(new NotebookScreen(stack, id));
    }
}
