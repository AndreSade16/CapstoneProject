package andreasaderi.capstone.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "recipe_ingredients")
@Getter
@Setter
@NoArgsConstructor
public class RecipeIngredient {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, name = "recipe_ingredient_id")
    private UUID recipeIngredientId;
    @ManyToOne
    @JoinColumn(nullable = false, name = "recipe_id")
    private Recipe recipe;
    @ManyToOne
    @JoinColumn(nullable = false, name = "ingredient_definition_id")
    private IngredientDefinition ingredientDefinition;
    @Column(nullable = false, name = "quantity_per_person")
    private double quantityPerPerson;


    public RecipeIngredient(Recipe recipe, IngredientDefinition ingredientDefinition, double quantityPerPerson) {
        this.recipe = recipe;
        this.ingredientDefinition = ingredientDefinition;
        this.quantityPerPerson = quantityPerPerson;
    }
}
