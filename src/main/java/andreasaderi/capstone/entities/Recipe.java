package andreasaderi.capstone.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, name = "recipe_id")
    private UUID recipeId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false, name = "image_url")
    private String imageUrl;
    @Column(nullable = false, name = "preparation_time")
    private double preparationTime;
    @Column(nullable = false, name = "cooking_time")
    private double cookingTime;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecipeDifficulty difficulty;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecipeCost cost;
    @Column(nullable = false)
    private String procedure;
    @Column(nullable = false, name = "visits_count")
    private long visitsCount;

    @OneToMany(
            mappedBy = "recipe",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe(String name, String description, String imageUrl, double preparationTime, double cookingTime, RecipeDifficulty difficulty, RecipeCost cost, String procedure) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.preparationTime = preparationTime;
        this.cookingTime = cookingTime;
        this.difficulty = difficulty;
        this.cost = cost;
        this.procedure = procedure;
        this.visitsCount = 0;
    }
}
