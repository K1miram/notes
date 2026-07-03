package kimiram.notes.item;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.util.function.Function;

import static kimiram.notes.Constants.MOD_ID;

public class ModItems {
    public static Item register(String name, Function<Item.Properties, Item> itemFactory, Item.Properties properties) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));

        Item item = itemFactory.apply(properties.setId(itemKey));

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    // TODO colored notebooks

    public static final Item NOTE = register("note", NoteItem::new, new Item.Properties().stacksTo(1));
    public static final Item FINALIZED_NOTE = register("finalized_note", FinalizedNoteItem::new, new Item.Properties());
    public static final Item NOTEBOOK = register(
            "notebook",
            properties -> new NotebookItem(properties, Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/notebook.png")),
            new Item.Properties().stacksTo(1)
    );
    public static final Item FINALIZED_NOTEBOOK = register(
            "finalized_notebook",
            properties -> new FinalizedNotebookItem(properties, Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/notebook.png")),
            new Item.Properties()
    );

    public static void initialize() {
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.accept(NOTE);
            entries.accept(NOTEBOOK);
        });
    }
}
