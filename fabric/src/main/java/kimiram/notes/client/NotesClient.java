package kimiram.notes.client;

import kimiram.notes.client.gui.screen.FabricNoteScreen;
import kimiram.notes.client.gui.screen.FinalizedNoteScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class NotesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

    }

    public static void openNote(ItemStack stack, InteractionHand hand) {
        Minecraft.getInstance().setScreen(new NoteScreen(stack, newStack -> saveNote(newStack, hand), newStack -> finalizeNote(newStack, hand)));
    }

    private static void saveNote(ItemStack stack, InteractionHand hand) {
        ClientPlayNetworking.send(new SaveNoteC2SPayload(stack, hand));
    }

    private static void finalizeNote(ItemStack stack, InteractionHand hand) {
        ClientPlayNetworking.send(new FinalizeNoteC2SPayload(stack, hand));
    }

    public static void openFinalizedNote(ItemStack stack) {
        Minecraft.getInstance().setScreen(new FinalizedNoteScreen(stack));
    }
}
