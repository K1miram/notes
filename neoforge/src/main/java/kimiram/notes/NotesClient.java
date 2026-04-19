package kimiram.notes;

import kimiram.notes.client.gui.screen.FinalizedNoteScreen;
import kimiram.notes.client.gui.screen.NoteScreen;
import kimiram.notes.client.gui.screen.NotebookScreen;
import kimiram.notes.networking.FinalizeNoteC2SPayload;
import kimiram.notes.networking.FinalizeNotebookC2SPayload;
import kimiram.notes.networking.RemovePageFromNotebookC2SPayload;
import kimiram.notes.networking.SaveNoteC2SPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class NotesClient {
    public static void openNote(ItemStack stack, InteractionHand hand) {
        Minecraft.getInstance().setScreen(new NoteScreen(stack, newStack -> saveNote(newStack, hand), newStack -> finalizeNote(newStack, hand)));
    }

    public static void saveNote(ItemStack stack, InteractionHand hand) {
        ClientPacketDistributor.sendToServer(new SaveNoteC2SPayload(stack, hand));
    }

    public static void finalizeNote(ItemStack stack, InteractionHand hand) {
        ClientPacketDistributor.sendToServer(new FinalizeNoteC2SPayload(stack, hand));
    }

    public static void openFinalizedNote(ItemStack stack) {
        Minecraft.getInstance().setScreen(new FinalizedNoteScreen(stack));
    }

    public static void openNotebook(ItemStack stack, InteractionHand hand, Identifier id) {
        Minecraft.getInstance().setScreen(new NotebookScreen(
                stack,
                id,
                pageIndex -> removePage(hand, pageIndex),
                () -> finalizeNotebook(hand))
        );
    }

    public static void removePage(InteractionHand hand, int pageIndex) {
        ClientPacketDistributor.sendToServer(new RemovePageFromNotebookC2SPayload(hand, pageIndex));
    }

    public static void finalizeNotebook(InteractionHand hand) {
        ClientPacketDistributor.sendToServer(new FinalizeNotebookC2SPayload(hand));
    }

    public static void openFinalizedNotebook(ItemStack stack, Identifier id) {
        Minecraft.getInstance().setScreen(new NotebookScreen(stack, id));
    }
}
