package kimiram.notes.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import static kimiram.notes.Constants.MOD_ID;

public class ModRecipes {
    public static final RecipeSerializer<NoteCloningRecipe> NOTE_CLONING_RECIPE_RECIPE_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(MOD_ID, "crafting_special_notecloning"),
            new CustomRecipe.Serializer<>(NoteCloningRecipe::new));

    public static final RecipeType<NoteCloningRecipe> NOTE_CLONING_RECIPE_TYPE = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            Identifier.fromNamespaceAndPath(MOD_ID, "crafting_special_notecloning"),
            NoteCloningRecipe.Type.INSTANCE);

    public static void initialize() {

    }
}
