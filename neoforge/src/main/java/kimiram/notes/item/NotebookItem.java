package kimiram.notes.item;

import kimiram.notes.NotesClient;
import kimiram.notes.item.component.FinalizedNoteContent;
import kimiram.notes.item.component.NotebookContent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static kimiram.notes.Notes.FINALIZED_NOTE;
import static kimiram.notes.item.component.ModDataComponents.FINALIZED_NOTE_COMPONENT_TYPE;
import static kimiram.notes.item.component.ModDataComponents.NOTEBOOK_COMPONENT_TYPE;

public class NotebookItem extends Item {
    private final ResourceLocation backgroundTextureId;

    public NotebookItem(Properties properties, ResourceLocation id) {
        super(properties);
        backgroundTextureId = id;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        ItemStack stackInOffhand = player.getItemInHand(InteractionHand.OFF_HAND);
        if (stackInOffhand.is(FINALIZED_NOTE)) {
            NotebookContent content = stack.get(NOTEBOOK_COMPONENT_TYPE);
            List<FinalizedNoteContent> pages = new ArrayList<>();
            if (content != null) {
                pages.addAll(content.pages());
            }
            if (pages.size() < 100) {
                FinalizedNoteContent newPage = stackInOffhand.get(FINALIZED_NOTE_COMPONENT_TYPE);
                pages.add(newPage);
                stack.set(NOTEBOOK_COMPONENT_TYPE, new NotebookContent(pages));
                player.setItemInHand(InteractionHand.OFF_HAND, stackInOffhand.copyWithCount(stackInOffhand.getCount() - 1));
                return InteractionResult.SUCCESS;
            }
        }
        if (level.isClientSide()) {
            NotesClient.openNotebook(stack, usedHand, backgroundTextureId);
        }
        return InteractionResult.SUCCESS;
    }
}
