package kimiram.notes;

import kimiram.notes.client.gui.screen.FinalizedNoteScreen;
import kimiram.notes.client.gui.screen.NeoForgeNoteScreen;
import kimiram.notes.item.FinalizedNoteItem;
import kimiram.notes.item.NoteItem;
import kimiram.notes.item.component.FinalizedNoteContent;
import kimiram.notes.item.component.NoteContent;
import kimiram.notes.networking.FinalizeNoteC2SPayload;
import kimiram.notes.networking.SaveNoteC2SPayload;
import kimiram.notes.recipe.NoteCloningRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import static kimiram.notes.Constants.MOD_ID;

@Mod(MOD_ID)
public class Notes {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<NoteItem> NOTE = ITEMS.registerItem(
            "note", (properties) -> new NoteItem(properties.stacksTo(1)));

    public static final DeferredItem<FinalizedNoteItem> FINALIZED_NOTE = ITEMS.registerItem(
            "finalized_note", FinalizedNoteItem::new
    );

    public static final RecipeSerializer<NoteCloningRecipe> NOTE_CLONING_RECIPE_RECIPE_SERIALIZER = new CustomRecipe.Serializer<>(NoteCloningRecipe::new);

    public static final DataComponentType<NoteContent> NOTE_COMPONENT_TYPE = DataComponentType.<NoteContent>builder().persistent(NoteContent.CODEC).build();
    public static final DataComponentType<FinalizedNoteContent> FINALIZED_NOTE_COMPONENT_TYPE = DataComponentType.<FinalizedNoteContent>builder().persistent(FinalizedNoteContent.CODEC).build();

    public Notes(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);

        modEventBus.addListener(this::registerPackets);

        modEventBus.addListener(this::onRegisterEvent);
    }

    public void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MOD_ID);

        registrar.playToServer(
                SaveNoteC2SPayload.TYPE,
                SaveNoteC2SPayload.CODEC,
                (payload, context) -> {
                    Player player = context.player();
                    if (player.getItemInHand(InteractionHand.MAIN_HAND).is(NOTE.get())) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, payload.stack());
                    } else if (player.getItemInHand(InteractionHand.OFF_HAND).is(NOTE.get())) {
                        player.setItemInHand(InteractionHand.OFF_HAND, payload.stack());
                    }
                });

        registrar.playToServer(
                FinalizeNoteC2SPayload.TYPE,
                FinalizeNoteC2SPayload.CODEC,
                (payload, context) -> {
                    Player player = context.player();
                    ItemStack stack = payload.stack();
                    NoteContent noteContent = stack.get(NOTE_COMPONENT_TYPE);
                    stack.remove(NOTE_COMPONENT_TYPE);
                    ItemStack newStack = stack.transmuteCopy(FINALIZED_NOTE);
                    if (noteContent != null) {
                        FinalizedNoteContent finalizedNoteContent = new FinalizedNoteContent(Filterable.passThrough(Component.literal(noteContent.text())), noteContent.images());
                        newStack.set(FINALIZED_NOTE_COMPONENT_TYPE, finalizedNoteContent);
                    }
                    if (player.getItemInHand(InteractionHand.MAIN_HAND).is(NOTE)) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, newStack);
                    } else if (player.getItemInHand(InteractionHand.OFF_HAND).is(NOTE)) {
                        player.setItemInHand(InteractionHand.OFF_HAND, newStack);
                    }
                });
    }

    public void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(NOTE.get());
        }
    }

    public void onRegisterEvent(RegisterEvent event) {
        event.register(Registries.RECIPE_SERIALIZER, helper -> {
            helper.register(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "crafting_special_notecloning"),
                    NOTE_CLONING_RECIPE_RECIPE_SERIALIZER
            );
        });

        event.register(Registries.RECIPE_TYPE, helper -> {
            helper.register(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "crafting_special_notecloning"),
                    NoteCloningRecipe.Type.INSTANCE
            );
        });

        event.register(Registries.DATA_COMPONENT_TYPE, helper -> {
            helper.register(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "note_content"),
                    NOTE_COMPONENT_TYPE
            );
            helper.register(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "finalized_note_content"),
                    FINALIZED_NOTE_COMPONENT_TYPE
            );
        });
    }

    public static class NotesClient {
        public static void openNote(ItemStack stack) {
            Minecraft.getInstance().setScreen(new NeoForgeNoteScreen(stack));
        }

        public static void openFinalizedNote(ItemStack stack) {
            Minecraft.getInstance().setScreen(new FinalizedNoteScreen(stack, FINALIZED_NOTE_COMPONENT_TYPE));
        }
    }
}
