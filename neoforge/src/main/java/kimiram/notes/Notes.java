package kimiram.notes;

import kimiram.notes.client.gui.screen.FinalizedNoteScreen;
import kimiram.notes.client.gui.screen.NeoForgeNoteScreen;
import kimiram.notes.item.FinalizedNoteItem;
import kimiram.notes.item.NoteItem;
import kimiram.notes.networking.FinalizeNoteC2SPacket;
import kimiram.notes.networking.SaveNoteC2SPacket;
import kimiram.notes.recipe.NoteCloningRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import static kimiram.notes.Constants.MOD_ID;

@Mod(MOD_ID)
public class Notes {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<Item> NOTE = ITEMS.registerItem(
            "note", (properties) -> new NoteItem(properties.stacksTo(1)));

    public static final DeferredItem<Item> FINALIZED_NOTE = ITEMS.registerItem(
            "finalized_note", (FinalizedNoteItem::new)
    );

    public static final RecipeSerializer<NoteCloningRecipe> NOTE_CLONING_RECIPE_RECIPE_SERIALIZER = new SimpleCraftingRecipeSerializer<>(NoteCloningRecipe::new);

    public Notes(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);

        modEventBus.addListener(this::registerPackets);

        modEventBus.addListener(this::registerCrafts);
    }

    public void registerPackets(RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(MOD_ID);

        registrar.play(SaveNoteC2SPacket.ID, SaveNoteC2SPacket::new, handler -> handler
                .server(SaveNoteC2SPacket::handle));

        registrar.play(FinalizeNoteC2SPacket.ID, FinalizeNoteC2SPacket::new, handler -> handler
                .server(FinalizeNoteC2SPacket::handle));
    }

    public void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(NOTE.get());
        }
    }

    public void registerCrafts(RegisterEvent event) {
        event.register(Registries.RECIPE_SERIALIZER, helper -> {
            helper.register(
                    new ResourceLocation(MOD_ID, "crafting_special_notecloning"),
                    NOTE_CLONING_RECIPE_RECIPE_SERIALIZER
            );
        });

        event.register(Registries.RECIPE_TYPE, helper -> {
            helper.register(
                    new ResourceLocation(MOD_ID, "crafting_special_notecloning"),
                    NoteCloningRecipe.Type.INSTANCE
            );
        });
    }

    public static class NotesClient {
        public static void openNote(ItemStack stack) {
            Minecraft.getInstance().setScreen(new NeoForgeNoteScreen(stack));
        }

        public static void openFinalizedNote(ItemStack stack) {
            Minecraft.getInstance().setScreen(new FinalizedNoteScreen(stack));
        }
    }
}
