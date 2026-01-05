package kimiram.notes.client;

import kimiram.notes.client.gui.screen.FabricNoteScreen;
import kimiram.notes.client.gui.screen.FinalizedNoteScreen;
import kimiram.notes.item.ModDataComponents;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class NotesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

    }

    public static void openNote(ItemStack stack) {
        Minecraft.getInstance().setScreen(new FabricNoteScreen(stack));
    }

    public static void openFinalizedNote(ItemStack stack) {
        Minecraft.getInstance().setScreen(new FinalizedNoteScreen(stack, ModDataComponents.FINALIZED_NOTE_COMPONENT_TYPE));
    }
}
