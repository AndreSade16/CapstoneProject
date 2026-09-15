package andreasaderi.capstone.services;


import andreasaderi.capstone.entities.*;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.exceptions.RecordAlreadyExistsException;
import andreasaderi.capstone.repositories.RecipeRepository;
import andreasaderi.capstone.specifications.RecipeSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeSpecification recipeSpecification;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private PantryItemService pantryItemService;

    @Mock
    private ShoppingListItemService shoppingListItemService;

    @InjectMocks
    private RecipeService recipeService;

    private User buildUser(Role role) {
        User user = new User("mario", "mario@email.com", "hashed-pw", "Mario", "Rossi", "avatar.png");
        user.setRole(role);
        return user;
    }

    private Recipe buildPublicRecipeWithIngredients() {
        Recipe recipe = new Recipe(
                "Carbonara", "Pasta romana", "img.png",
                10.0, 15.0, RecipeDifficulty.EASY, RecipeCost.CHEAP, "Cuoci la pasta..."
        );

        IngredientDefinition eggs = new IngredientDefinition(
                "Eggs", "Fresh eggs", "eggs.png", Category.DAIRY, Unit.UNITS,
                StorageLocation.REFRIGERATOR, 14, null, Set.of(Season.SPRING)
        );

        RecipeIngredient recipeIngredient = new RecipeIngredient(recipe, eggs, 2.0);
        recipe.setIngredients(List.of(recipeIngredient));

        return recipe;
    }

    @Test
    void savePersonalRecipeShouldCreateIndependentCopyWhenRecipeIsPublicAndNotAlreadySaved() {
        User user = buildUser(Role.USER);
        UUID recipeId = UUID.randomUUID();
        Recipe publicRecipe = buildPublicRecipeWithIngredients();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(publicRecipe));
        when(recipeRepository.existsByNameAndUser(publicRecipe.getName(), user)).thenReturn(false);
        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result = recipeService.savePersonalRecipe(user, recipeId);

        assertEquals(user, result.getUser());
        assertEquals(publicRecipe.getName(), result.getName());
        assertEquals(1, result.getIngredients().size());

        RecipeIngredient copiedIngredient = result.getIngredients().get(0);
        assertSame(result, copiedIngredient.getRecipe());
        assertNotSame(publicRecipe.getIngredients().get(0), copiedIngredient);

        verify(recipeRepository).save(any(Recipe.class));
    }

    @Test
    void savePersonalRecipeShouldThrowRecordAlreadyExistsExceptionWhenAlreadySaved() {
        User user = buildUser(Role.USER);
        UUID recipeId = UUID.randomUUID();
        Recipe publicRecipe = buildPublicRecipeWithIngredients();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(publicRecipe));
        when(recipeRepository.existsByNameAndUser(publicRecipe.getName(), user)).thenReturn(true);

        assertThrows(RecordAlreadyExistsException.class,
                () -> recipeService.savePersonalRecipe(user, recipeId));

        verify(recipeRepository, never()).save(any());
    }

    @Test
    void savePersonalRecipeShouldThrowNotFoundExceptionWhenRecipeDoesNotExist() {
        User user = buildUser(Role.USER);
        UUID recipeId = UUID.randomUUID();

        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> recipeService.savePersonalRecipe(user, recipeId));

        verify(recipeRepository, never()).existsByNameAndUser(anyString(), any());
        verify(recipeRepository, never()).save(any());
    }

    @Test
    void savePersonalRecipeShouldThrowAuthorizationDeniedExceptionWhenRecipeBelongsToAnotherUser() {
        User requestingUser = buildUser(Role.USER);
        User owner = buildUser(Role.USER);

        ReflectionTestUtils.setField(requestingUser, "userId", UUID.randomUUID());

        ReflectionTestUtils.setField(owner, "userId", UUID.randomUUID());

        UUID recipeId = UUID.randomUUID();
        Recipe privateRecipe = buildPublicRecipeWithIngredients();
        privateRecipe.setUser(owner);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(privateRecipe));
        assertThrows(AuthorizationDeniedException.class, () -> recipeService.savePersonalRecipe(requestingUser, recipeId));
        verify(recipeRepository, never()).save(any());
    }
}