package andreasaderi.capstone.specifications;

import andreasaderi.capstone.entities.Category;
import andreasaderi.capstone.entities.IngredientDefinition;
import andreasaderi.capstone.entities.Season;
import andreasaderi.capstone.requestDTOs.IngredientDefinitionFiltersDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IngredientDefinitionSpecification {

    public Specification<IngredientDefinition> specificationIngredientDefinitionBuilder(IngredientDefinitionFiltersDTO filters) {
        Specification<IngredientDefinition> spec = (root, query, cb) -> cb.conjunction();

        if (filters.name() != null && !filters.name().isBlank()) {
            spec = spec.and(hasName(filters.name()));
        }

        if (filters.category() != null) {
            spec = spec.and(hasCategory(filters.category()));
        }

        if (filters.minShelfLifeDays() != null) {
            spec = spec.and(hasShelfLifeGreaterThan(filters.minShelfLifeDays()));
        }

        if (filters.maxShelfLifeDays() != null) {
            spec = spec.and(hasShelfLifeLessThan(filters.maxShelfLifeDays()));
        }

        if (filters.seasonality() != null && !filters.seasonality().isEmpty()) {

            for (Season season : filters.seasonality()) {
                spec = spec.and(hasSeason(season));
            }

        }

        return spec;
    }

    public Specification<IngredientDefinition> hasName(String name) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }


    public Specification<IngredientDefinition> hasCategory(Category category) {
        return (root, query, cb) ->
                cb.equal(
                        root.get("category"), category
                );
    }

    public Specification<IngredientDefinition> hasShelfLifeGreaterThan(Integer minShelfLifeDays) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("shelfLifeDays"), minShelfLifeDays
                );
    }

    public Specification<IngredientDefinition> hasShelfLifeLessThan(Integer maxShelfLifeDays) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("shelfLifeDays"), maxShelfLifeDays
                );
    }


    public Specification<IngredientDefinition> hasSeason(Season season) {
        return (root, query, cb) ->
                cb.isMember(season, root.get("seasonality"));
    }
}
