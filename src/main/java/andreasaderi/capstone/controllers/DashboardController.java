package andreasaderi.capstone.controllers;

import andreasaderi.capstone.entities.PantryItem;
import andreasaderi.capstone.entities.Recipe;
import andreasaderi.capstone.entities.ShoppingList;
import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.responseDTOs.DashboardDTO;
import andreasaderi.capstone.responseDTOs.PantryItemResponseDTO;
import andreasaderi.capstone.responseDTOs.RecipeCardDTO;
import andreasaderi.capstone.responseDTOs.ShoppingListResponseDTO;
import andreasaderi.capstone.services.PantryItemService;
import andreasaderi.capstone.services.RecipeService;
import andreasaderi.capstone.services.ShoppingListService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final PantryItemService pantryItemService;
    private final ShoppingListService shoppingListService;
    private final RecipeService recipeService;


    public DashboardController(PantryItemService pantryItemService, ShoppingListService shoppingListService, RecipeService recipeService) {
        this.pantryItemService = pantryItemService;
        this.shoppingListService = shoppingListService;
        this.recipeService = recipeService;
    }

    @GetMapping
    public DashboardDTO getDashBoard(@AuthenticationPrincipal User user) {
        Page<PantryItem> pantryItemsPage = pantryItemService.findByUser(user, 0, 6, "expirationDate", Sort.Direction.ASC);
        System.out.println(pantryItemsPage);
        List<PantryItemResponseDTO> pantryItemResponseDTOs = pantryItemsPage.stream().map(pantryItem -> new PantryItemResponseDTO(pantryItem.getPantryItemId(), pantryItem.getIngredientDefinition().getIngredientDefinitionId(), pantryItem.getIngredientDefinition().getName(), pantryItem.getIngredientDefinition().getImageUrl(), pantryItem.getQuantity(), pantryItem.getIngredientDefinition().getUnit(), pantryItem.getStorageLocation(), pantryItem.getExpirationDate(), LocalDate.now().until(pantryItem.getExpirationDate(), ChronoUnit.DAYS), pantryItem.getIngredientDefinition().getCategory())).toList();

        ShoppingListResponseDTO activeShoppingList;
        try {
            ShoppingList shoppingList = shoppingListService.findByUserAndActive(user);
            activeShoppingList = new ShoppingListResponseDTO(shoppingList.getShoppingListId(), shoppingList.getCreatedAt(), shoppingList.getUpdatedAt(), shoppingList.getShoppingListStatus(), shoppingList.getItems());
        } catch (RuntimeException exception) {
            activeShoppingList = new ShoppingListResponseDTO(null, null, null, null, null);
        }


        List<Recipe> recipesListForUser = recipeService.findMostRelevantForUser(user, 1, 6);

        List<RecipeCardDTO> recipeCardDTOs = recipesListForUser.stream().map(recipe -> new RecipeCardDTO(recipe.getRecipeId(), recipe.getName(), recipe.getImageUrl(), recipe.getCookingTime() + recipe.getPreparationTime(), recipe.getDifficulty(), recipe.getCost())).toList();


        return new DashboardDTO(pantryItemResponseDTOs, activeShoppingList, recipeCardDTOs);
    }
}
