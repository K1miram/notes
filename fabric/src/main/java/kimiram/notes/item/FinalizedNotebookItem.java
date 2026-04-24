package kimiram.notes.item;

import kimiram.notes.client.NotesClient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FinalizedNotebookItem extends Item {
    private final ResourceLocation backgroundTextureId;

    public FinalizedNotebookItem(Properties properties, ResourceLocation id) {
        super(properties);
        backgroundTextureId = id;
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide()) {
            NotesClient.openFinalizedNotebook(stack, backgroundTextureId);
        }
        return InteractionResult.SUCCESS;
    }
}
