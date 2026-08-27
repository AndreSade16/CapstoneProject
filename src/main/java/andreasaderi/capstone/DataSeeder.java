package andreasaderi.capstone;


import andreasaderi.capstone.entities.*;
import andreasaderi.capstone.repositories.IngredientDefinitionRepository;
import andreasaderi.capstone.repositories.RecipeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final IngredientDefinitionRepository ingredientRepo;
    private final RecipeRepository recipeRepo;

    public DataSeeder(IngredientDefinitionRepository ingredientRepo, RecipeRepository recipeRepo) {
        this.ingredientRepo = ingredientRepo;
        this.recipeRepo = recipeRepo;
    }

    @Override
    public void run(String... args) {
        if (ingredientRepo.count() > 0) {
            System.out.println("Database already seeded, skipping...");
            return;
        }

        System.out.println("Seeding database...");

        // ====================== 25 INGREDIENT DEFINITIONS ======================
        List<IngredientDefinition> ingredients = List.of(
                createIng("Tomato", "Fresh ripe red tomato", "https://example.com/tomato.jpg",
                        Category.VEGETABLE, Unit.GRAMS, StorageLocation.REFRIGERATOR, 7,
                        "Sauces, salads, soups", Set.of(Season.SUMMER, Season.AUTUMN)),

                createIng("Basil", "Fresh aromatic basil leaves", "https://example.com/basil.jpg",
                        Category.VEGETABLE, Unit.GRAMS, StorageLocation.REFRIGERATOR, 5,
                        "Pesto, garnish, sauces", Set.of(Season.SPRING, Season.SUMMER)),

                createIng("Mozzarella", "Fresh mozzarella cheese", "https://example.com/mozzarella.jpg",
                        Category.DAIRY, Unit.GRAMS, StorageLocation.REFRIGERATOR, 10,
                        "Pizza, salads, sandwiches", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Parmesan", "Aged Parmesan cheese", "https://example.com/parmesan.jpg",
                        Category.DAIRY, Unit.GRAMS, StorageLocation.REFRIGERATOR, 90,
                        "Grating over pasta and soups", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Guanciale", "Cured pork jowl", "https://example.com/guanciale.jpg",
                        Category.MEAT, Unit.GRAMS, StorageLocation.REFRIGERATOR, 30,
                        "Carbonara, Amatriciana", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Eggs", "Fresh chicken eggs", "https://example.com/eggs.jpg",
                        Category.OTHER, Unit.UNITS, StorageLocation.REFRIGERATOR, 21,
                        "Baking, frying, carbonara", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Spaghetti", "Dried spaghetti pasta", "https://example.com/spaghetti.jpg",
                        Category.GRAIN, Unit.GRAMS, StorageLocation.PANTRY, 730,
                        "Pasta dishes", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Penne", "Dried penne pasta", "https://example.com/penne.jpg",
                        Category.GRAIN, Unit.GRAMS, StorageLocation.PANTRY, 730,
                        "Pasta dishes", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Arborio Rice", "Short-grain rice for risotto", "https://example.com/arborio.jpg",
                        Category.GRAIN, Unit.GRAMS, StorageLocation.PANTRY, 365,
                        "Risotto", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("All-Purpose Flour", "Wheat flour type 00", "https://example.com/flour.jpg",
                        Category.GRAIN, Unit.GRAMS, StorageLocation.PANTRY, 180,
                        "Pasta, pizza, baking", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Extra Virgin Olive Oil", "High quality olive oil", "https://example.com/oliveoil.jpg",
                        Category.OTHER, Unit.MILLILITERS, StorageLocation.PANTRY, 540,
                        "Cooking, dressing", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Garlic", "Fresh garlic bulbs", "https://example.com/garlic.jpg",
                        Category.VEGETABLE, Unit.UNITS, StorageLocation.PANTRY, 30,
                        "Sauté, sauces", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Onion", "Yellow onion", "https://example.com/onion.jpg",
                        Category.VEGETABLE, Unit.UNITS, StorageLocation.PANTRY, 30,
                        "Sauté, soups, sauces", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Carrot", "Fresh carrots", "https://example.com/carrot.jpg",
                        Category.VEGETABLE, Unit.GRAMS, StorageLocation.REFRIGERATOR, 14,
                        "Soups, stews, side dishes", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Celery", "Fresh celery stalks", "https://example.com/celery.jpg",
                        Category.VEGETABLE, Unit.GRAMS, StorageLocation.REFRIGERATOR, 10,
                        "Soups, stews, soffritto", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Potato", "Yellow potatoes", "https://example.com/potato.jpg",
                        Category.VEGETABLE, Unit.GRAMS, StorageLocation.PANTRY, 30,
                        "Side dishes, purees", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Zucchini", "Fresh green zucchini", "https://example.com/zucchini.jpg",
                        Category.VEGETABLE, Unit.GRAMS, StorageLocation.REFRIGERATOR, 7,
                        "Side dishes, pasta, fritters", Set.of(Season.SUMMER)),

                createIng("Mushroom", "Fresh button mushrooms", "https://example.com/mushroom.jpg",
                        Category.VEGETABLE, Unit.GRAMS, StorageLocation.REFRIGERATOR, 5,
                        "Risotto, sauces, side dishes", Set.of(Season.SPRING, Season.AUTUMN)),

                createIng("Chicken Breast", "Boneless chicken breast", "https://example.com/chicken.jpg",
                        Category.MEAT, Unit.GRAMS, StorageLocation.REFRIGERATOR, 3,
                        "Main courses", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Salmon Fillet", "Fresh salmon fillet", "https://example.com/salmon.jpg",
                        Category.FISH, Unit.GRAMS, StorageLocation.REFRIGERATOR, 2,
                        "Main courses", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Butter", "Unsalted butter", "https://example.com/butter.jpg",
                        Category.DAIRY, Unit.GRAMS, StorageLocation.REFRIGERATOR, 30,
                        "Cooking, baking", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Heavy Cream", "Fresh heavy cream", "https://example.com/cream.jpg",
                        Category.DAIRY, Unit.MILLILITERS, StorageLocation.REFRIGERATOR, 14,
                        "Sauces, desserts", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Salt", "Fine table salt", "https://example.com/salt.jpg",
                        Category.OTHER, Unit.GRAMS, StorageLocation.PANTRY, 999,
                        "Seasoning", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Black Pepper", "Ground black pepper", "https://example.com/pepper.jpg",
                        Category.OTHER, Unit.GRAMS, StorageLocation.PANTRY, 365,
                        "Seasoning", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER)),

                createIng("Lemon", "Fresh lemon", "https://example.com/lemon.jpg",
                        Category.FRUIT, Unit.UNITS, StorageLocation.REFRIGERATOR, 14,
                        "Dressings, desserts, fish", Set.of(Season.SPRING, Season.SUMMER, Season.AUTUMN, Season.WINTER))
        );

        ingredientRepo.saveAll(ingredients);
        System.out.println("Saved " + ingredients.size() + " ingredients");

        // Create a map for easy lookup by name
        Map<String, IngredientDefinition> ingMap = new HashMap<>();
        ingredients.forEach(i -> ingMap.put(i.getName(), i));

        // ====================== 8 RECIPES ======================

        // 1. Spaghetti Carbonara
        Recipe carbonara = createRecipe(
                "Spaghetti Carbonara",
                "Classic Roman pasta with guanciale, eggs and pecorino",
                "https://example.com/carbonara.jpg",
                10, 15, RecipeDifficulty.MEDIUM, RecipeCost.NORMAL,
                "1. Cook spaghetti in salted water.\n2. Fry guanciale until crispy.\n3. Mix eggs with grated cheese and pepper.\n4. Combine everything off the heat."
        );
        addIngredient(carbonara, ingMap.get("Spaghetti"), 100);
        addIngredient(carbonara, ingMap.get("Guanciale"), 40);
        addIngredient(carbonara, ingMap.get("Eggs"), 1);
        addIngredient(carbonara, ingMap.get("Parmesan"), 30);
        addIngredient(carbonara, ingMap.get("Black Pepper"), 2);
        addIngredient(carbonara, ingMap.get("Salt"), 3);

        // 2. Margherita Pizza (simplified as a recipe)
        Recipe margherita = createRecipe(
                "Margherita Pizza",
                "Classic Neapolitan pizza with tomato, mozzarella and basil",
                "https://example.com/margherita.jpg",
                20, 12, RecipeDifficulty.MEDIUM, RecipeCost.NORMAL,
                "1. Prepare pizza dough.\n2. Spread tomato sauce.\n3. Add mozzarella and bake.\n4. Finish with fresh basil and olive oil."
        );
        addIngredient(margherita, ingMap.get("All-Purpose Flour"), 150);
        addIngredient(margherita, ingMap.get("Tomato"), 80);
        addIngredient(margherita, ingMap.get("Mozzarella"), 100);
        addIngredient(margherita, ingMap.get("Basil"), 5);
        addIngredient(margherita, ingMap.get("Extra Virgin Olive Oil"), 15);
        addIngredient(margherita, ingMap.get("Salt"), 3);

        // 3. Mushroom Risotto
        Recipe risotto = createRecipe(
                "Mushroom Risotto",
                "Creamy risotto with fresh mushrooms and Parmesan",
                "https://example.com/risotto.jpg",
                10, 25, RecipeDifficulty.MEDIUM, RecipeCost.NORMAL,
                "1. Sauté onion and mushrooms.\n2. Toast rice.\n3. Add broth gradually.\n4. Finish with butter and Parmesan."
        );
        addIngredient(risotto, ingMap.get("Arborio Rice"), 80);
        addIngredient(risotto, ingMap.get("Mushroom"), 100);
        addIngredient(risotto, ingMap.get("Onion"), 0.3);
        addIngredient(risotto, ingMap.get("Butter"), 20);
        addIngredient(risotto, ingMap.get("Parmesan"), 30);
        addIngredient(risotto, ingMap.get("Extra Virgin Olive Oil"), 10);

        // 4. Grilled Salmon with Lemon
        Recipe salmon = createRecipe(
                "Grilled Salmon with Lemon",
                "Simple and healthy grilled salmon fillet",
                "https://example.com/salmon-dish.jpg",
                10, 12, RecipeDifficulty.EASY, RecipeCost.NORMAL,
                "1. Season salmon with salt, pepper and olive oil.\n2. Grill for 5-6 minutes per side.\n3. Serve with lemon wedges."
        );
        addIngredient(salmon, ingMap.get("Salmon Fillet"), 180);
        addIngredient(salmon, ingMap.get("Lemon"), 0.5);
        addIngredient(salmon, ingMap.get("Extra Virgin Olive Oil"), 15);
        addIngredient(salmon, ingMap.get("Salt"), 2);
        addIngredient(salmon, ingMap.get("Black Pepper"), 1);

        // 5. Chicken with Vegetables
        Recipe chickenVeg = createRecipe(
                "Chicken with Roasted Vegetables",
                "Oven-roasted chicken breast with seasonal vegetables",
                "https://example.com/chicken-veg.jpg",
                15, 30, RecipeDifficulty.EASY, RecipeCost.NORMAL,
                "1. Season chicken and vegetables.\n2. Roast in the oven at 200°C for 30 minutes.\n3. Serve hot."
        );
        addIngredient(chickenVeg, ingMap.get("Chicken Breast"), 180);
        addIngredient(chickenVeg, ingMap.get("Potato"), 150);
        addIngredient(chickenVeg, ingMap.get("Carrot"), 80);
        addIngredient(chickenVeg, ingMap.get("Zucchini"), 100);
        addIngredient(chickenVeg, ingMap.get("Extra Virgin Olive Oil"), 20);
        addIngredient(chickenVeg, ingMap.get("Garlic"), 1);
        addIngredient(chickenVeg, ingMap.get("Salt"), 3);

        // 6. Creamy Tomato Pasta
        Recipe tomatoPasta = createRecipe(
                "Creamy Tomato Pasta",
                "Penne in a creamy tomato and basil sauce",
                "https://example.com/tomato-pasta.jpg",
                10, 20, RecipeDifficulty.EASY, RecipeCost.CHEAP,
                "1. Cook penne.\n2. Sauté garlic and tomato.\n3. Add cream and basil.\n4. Toss with pasta."
        );
        addIngredient(tomatoPasta, ingMap.get("Penne"), 100);
        addIngredient(tomatoPasta, ingMap.get("Tomato"), 150);
        addIngredient(tomatoPasta, ingMap.get("Heavy Cream"), 50);
        addIngredient(tomatoPasta, ingMap.get("Basil"), 8);
        addIngredient(tomatoPasta, ingMap.get("Garlic"), 1);
        addIngredient(tomatoPasta, ingMap.get("Parmesan"), 20);

        // 7. Zucchini Fritters
        Recipe fritters = createRecipe(
                "Zucchini Fritters",
                "Crispy pan-fried zucchini fritters",
                "https://example.com/fritters.jpg",
                15, 15, RecipeDifficulty.EASY, RecipeCost.CHEAP,
                "1. Grate zucchini and mix with flour, egg and cheese.\n2. Form small patties.\n3. Fry until golden."
        );
        addIngredient(fritters, ingMap.get("Zucchini"), 200);
        addIngredient(fritters, ingMap.get("All-Purpose Flour"), 40);
        addIngredient(fritters, ingMap.get("Eggs"), 1);
        addIngredient(fritters, ingMap.get("Parmesan"), 25);
        addIngredient(fritters, ingMap.get("Salt"), 2);
        addIngredient(fritters, ingMap.get("Extra Virgin Olive Oil"), 30);

        // 8. Classic Soffritto Base (as a simple recipe)
        Recipe soffritto = createRecipe(
                "Classic Italian Soffritto",
                "The essential base for many Italian sauces and soups",
                "https://example.com/soffritto.jpg",
                10, 15, RecipeDifficulty.VERY_EASY, RecipeCost.CHEAP,
                "1. Finely chop onion, carrot and celery.\n2. Sauté slowly in olive oil until soft and fragrant."
        );
        addIngredient(soffritto, ingMap.get("Onion"), 0.5);
        addIngredient(soffritto, ingMap.get("Carrot"), 60);
        addIngredient(soffritto, ingMap.get("Celery"), 50);
        addIngredient(soffritto, ingMap.get("Extra Virgin Olive Oil"), 25);
        addIngredient(soffritto, ingMap.get("Salt"), 2);

        // Save all recipes (cascade will save RecipeIngredients)
        recipeRepo.saveAll(List.of(carbonara, margherita, risotto, salmon, chickenVeg, tomatoPasta, fritters, soffritto));

        System.out.println("Saved 8 recipes with their ingredients");
        System.out.println("Seeding completed successfully!");
    }

    // ====================== HELPER METHODS ======================

    private IngredientDefinition createIng(String name, String description, String imageUrl,
                                           Category category, Unit unit, StorageLocation location,
                                           int shelfLifeDays, String alternativeUsages, Set<Season> seasons) {
        return new IngredientDefinition(name, description, imageUrl, category, unit,
                location, shelfLifeDays, alternativeUsages, seasons);
    }

    private Recipe createRecipe(String name, String description, String imageUrl,
                                double prepTime, double cookTime,
                                RecipeDifficulty difficulty, RecipeCost cost, String procedure) {
        return new Recipe(name, description, imageUrl, prepTime, cookTime, difficulty, cost, procedure);
    }

    private void addIngredient(Recipe recipe, IngredientDefinition ingredient, double quantityPerPerson) {
        RecipeIngredient ri = new RecipeIngredient(recipe, ingredient, quantityPerPerson);
        recipe.getIngredients().add(ri);
    }
}