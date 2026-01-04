package kimiram.notes.item;

import kimiram.notes.client.NotesClient;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FinalizedNoteItem extends Item {
    public FinalizedNoteItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide) {
            NotesClient.openFinalizedNote(stack);
            return InteractionResultHolder.pass(stack);
        }
        return InteractionResultHolder.success(stack);
    }
}
