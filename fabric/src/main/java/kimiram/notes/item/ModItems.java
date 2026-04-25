package kimiram.notes.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import static kimiram.notes.Constants.MOD_ID;

public class ModItems {
    public static Item register(String name, Item item) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    public static final Item NOTE = register("note", new NoteItem(new Item.Properties().stacksTo(1)));
    public static final Item FINALIZED_NOTE = register("finalized_note", new FinalizedNoteItem(new Item.Properties()));

    public static final Item NOTEBOOK = register(
            "notebook",
            new NotebookItem(new Item.Properties().stacksTo(1), ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/notebook.png"))
    );
    public static final Item FINALIZED_NOTEBOOK = register(
            "finalized_notebook",
            new FinalizedNotebookItem(new Item.Properties(), ResourceLocation.fromNamespaceAndPath(MOD_ID, "textures/gui/notebook.png"))
    );

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(NOTE);
            entries.accept(NOTEBOOK);
        });
    }
}
