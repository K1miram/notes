package kimiram.notes;

import kimiram.notes.item.FinalizedNoteItem;
import kimiram.notes.item.FinalizedNotebookItem;
import kimiram.notes.item.NoteItem;
import kimiram.notes.item.NotebookItem;
import kimiram.notes.item.component.FinalizedNoteContent;
import kimiram.notes.item.component.NoteContent;
import kimiram.notes.item.component.NotebookContent;
import kimiram.notes.networking.FinalizeNoteC2SPayload;
import kimiram.notes.networking.FinalizeNotebookC2SPayload;
import kimiram.notes.networking.RemovePageFromNotebookC2SPayload;
import kimiram.notes.networking.SaveNoteC2SPayload;
import kimiram.notes.recipe.NoteCloningRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;

import static kimiram.notes.Constants.MOD_ID;
import static kimiram.notes.item.component.ModDataComponents.*;

@Mod(MOD_ID)
public class Notes {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<NoteItem> NOTE = ITEMS.registerItem(
            "note", (properties) -> new NoteItem(properties.stacksTo(1)));

    public static final DeferredItem<FinalizedNoteItem> FINALIZED_NOTE = ITEMS.registerItem(
            "finalized_note", FinalizedNoteItem::new);

    public static final DeferredItem<NotebookItem> NOTEBOOK = ITEMS.registerItem(
            "notebook", properties -> new NotebookItem(
                    properties.stacksTo(1), Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/notebook.png"))
    );

    public static final DeferredItem<FinalizedNotebookItem> FINALIZED_NOTEBOOK = ITEMS.registerItem(
            "finalized_notebook", properties -> new FinalizedNotebookItem(
                    properties, Identifier.fromNamespaceAndPath(MOD_ID, "textures/gui/notebook.png"))
    );

    public static final RecipeSerializer<NoteCloningRecipe> NOTE_CLONING_RECIPE_RECIPE_SERIALIZER = new RecipeSerializer<>(
            NoteCloningRecipe.CODEC, NoteCloningRecipe.STREAM_CODEC);

    public Notes(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(this::addCreative);

        modEventBus.addListener(this::registerPackets);

        modEventBus.addListener(this::onRegisterEvent);
    }

    public void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MOD_ID);

        registrar.playToServer(SaveNoteC2SPayload.TYPE, SaveNoteC2SPayload.CODEC,
                (payload, context) -> {
                    Player player = context.player();
                    ItemStack stack = payload.stack();
                    InteractionHand hand = payload.hand();
                    player.setItemInHand(hand, stack);
                }
        );

        registrar.playToServer(FinalizeNoteC2SPayload.TYPE, FinalizeNoteC2SPayload.CODEC,
                (payload, context) -> {
                    Player player = context.player();
                    ItemStack stack = payload.stack();
                    InteractionHand hand = payload.hand();
                    NoteContent noteContent = stack.get(NOTE_COMPONENT_TYPE);
                    stack.remove(NOTE_COMPONENT_TYPE);
                    ItemStack newStack = stack.transmuteCopy(FINALIZED_NOTE);
                    if (noteContent != null) {
                        FinalizedNoteContent finalizedNoteContent = new FinalizedNoteContent(Filterable.passThrough(Component.literal(noteContent.text())), noteContent.images());
                        newStack.set(FINALIZED_NOTE_COMPONENT_TYPE, finalizedNoteContent);
                    }
                    player.setItemInHand(hand, newStack);
                }
        );

        registrar.playToServer(RemovePageFromNotebookC2SPayload.TYPE, RemovePageFromNotebookC2SPayload.CODEC,
                (payload, context) -> {
                    Player player = context.player();
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
                        removedPage = FINALIZED_NOTE.get().getDefaultInstance();
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
                }
        );

        registrar.playToServer(FinalizeNotebookC2SPayload.TYPE, FinalizeNotebookC2SPayload.CODEC,
                (payload, context) -> {
                    Player player = context.player();
                    InteractionHand hand = payload.hand();
                    ItemStack stack = player.getItemInHand(hand);
                    if (stack.is(NOTEBOOK)) {
                        stack = stack.transmuteCopy(FINALIZED_NOTEBOOK);
                    }
                    player.setItemInHand(hand, stack);
                }
        );
    }

    public void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(NOTE.get());
            event.accept(NOTEBOOK.get());
        }
    }

    public void onRegisterEvent(RegisterEvent event) {
        event.register(Registries.RECIPE_SERIALIZER, helper -> {
            helper.register(
                    Identifier.fromNamespaceAndPath(MOD_ID, "crafting_special_notecloning"),
                    NOTE_CLONING_RECIPE_RECIPE_SERIALIZER
            );
        });

        event.register(Registries.RECIPE_TYPE, helper -> {
            helper.register(
                    Identifier.fromNamespaceAndPath(MOD_ID, "crafting_special_notecloning"),
                    NoteCloningRecipe.Type.INSTANCE
            );
        });

        event.register(Registries.DATA_COMPONENT_TYPE, helper -> {
            helper.register(
                    Identifier.fromNamespaceAndPath(MOD_ID, "note_content"),
                    NOTE_COMPONENT_TYPE
            );
            helper.register(
                    Identifier.fromNamespaceAndPath(MOD_ID, "finalized_note_content"),
                    FINALIZED_NOTE_COMPONENT_TYPE
            );
            helper.register(
                    Identifier.fromNamespaceAndPath(MOD_ID, "notebook_content"),
                    NOTEBOOK_COMPONENT_TYPE
            );
        });
    }
}
