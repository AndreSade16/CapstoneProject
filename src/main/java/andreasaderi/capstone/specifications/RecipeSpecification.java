package andreasaderi.capstone.specifications;

import andreasaderi.capstone.entities.Recipe;
import andreasaderi.capstone.entities.RecipeCost;
import andreasaderi.capstone.entities.RecipeDifficulty;
import andreasaderi.capstone.requestDTOs.RecipeFiltersDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class RecipeSpecification {

    public Specification<Recipe> specificationRecipeBuilder(RecipeFiltersDTO filters) {
        Specification<Recipe> spec = (root, query, cb) -> cb.conjunction();

        if (filters.name() != null && !filters.name().isBlank()) {
            spec = spec.and(hasName(filters.name()));
        }


        if (filters.minTime() != null) {
            spec = spec.and(hasTimeGreaterThan(filters.minTime()));
        }

        if (filters.maxTime() != null) {
            spec = spec.and(hasTimeLessThan(filters.maxTime()));
        }

        if (filters.difficulty() != null && !filters.difficulty().isEmpty()) {

            Specification<Recipe> difficultySpec = null;

            for (RecipeDifficulty difficulty : filters.difficulty()) {
                if (difficultySpec == null) {
                    difficultySpec = hasDifficulty(difficulty);
                } else {
                    difficultySpec = difficultySpec.or(hasDifficulty(difficulty));
                }
            }

            spec = spec.and(difficultySpec);
        }

        if (filters.cost() != null && !filters.cost().isEmpty()) {

            Specification<Recipe> costSpec = null;

            for (RecipeCost cost : filters.cost()) {
                if (costSpec == null) {
                    costSpec = hasCost(cost);
                } else {
                    costSpec = costSpec.or(hasCost(cost));
                }
            }

            spec = spec.and(costSpec);
        }

        return spec;
    }

    public Specification<Recipe> hasName(String name) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }


    public Specification<Recipe> hasDifficulty(RecipeDifficulty difficulty) {
        return (root, query, cb) ->
                cb.equal(
                        root.get("difficulty"), difficulty
                );
    }

    public Specification<Recipe> hasTimeGreaterThan(Double minTime) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        cb.sum(
                                root.get("preparationTime"),
                                root.get("cookingTime")
                        ),
                        minTime
                );
    }

    public Specification<Recipe> hasTimeLessThan(Double maxTime) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        cb.sum(
                                root.get("preparationTime"),
                                root.get("cookingTime")
                        ),
                        maxTime
                );
    }

    public Specification<Recipe> hasCost(RecipeCost cost) {
        return (root, query, cb) ->
                cb.equal(root.get("cost"), cost);
    }
}
