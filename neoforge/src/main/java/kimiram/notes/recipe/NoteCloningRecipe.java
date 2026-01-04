package kimiram.notes.recipe;

import kimiram.notes.Notes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NoteCloningRecipe extends CustomRecipe {
    public NoteCloningRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingContainer container, @NotNull Level level) {
        List<ItemStack> stacks = container.getItems();
        boolean bl1 = false, bl2 = false;
        for (ItemStack stack: stacks) {
            if (!stack.isEmpty()) {
                if (stack.is(Notes.FINALIZED_NOTE)) {
                    if (bl1) {
                        return false;
                    }

                    bl1 = true;
                } else {
                    if (!stack.is(Notes.NOTE)) {
                        return false;
                    }

                    bl2 = true;
                }
            }
        }
        return bl1 && bl2;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, @NotNull RegistryAccess registryAccess) {
        List<ItemStack> stacks = container.getItems();
        ItemStack finNote = ItemStack.EMPTY;
        int cnt = 0;
        for (ItemStack stack: stacks) {
            if (!stack.isEmpty()) {
                if (stack.is(Notes.FINALIZED_NOTE)) {
                    if (!finNote.isEmpty()) {
                        return ItemStack.EMPTY;
                    }

                    finNote = stack;
                } else {
                    if (!stack.is(Notes.NOTE)) {
                        return ItemStack.EMPTY;
                    }

                    cnt++;
                }
            }
        }

        if (cnt > 0) {
            return finNote.copyWithCount(cnt);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> defaultedList = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

        for (int i = 0; i < defaultedList.size(); i++) {
            ItemStack itemStack = container.getItem(i);
            if (itemStack.is(Notes.FINALIZED_NOTE)) {
                defaultedList.set(i, itemStack.copyWithCount(1));
                break;
            }
        }

        return defaultedList;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Notes.NOTE_CLONING_RECIPE_RECIPE_SERIALIZER;
    }

    public static class Type implements RecipeType<NoteCloningRecipe> {
        public static final Type INSTANCE = new Type();

        private Type() {
        }

        @Override
        public String toString() {
            return "crafting_special_notecloning";
        }
    }
}
