package andreasaderi.capstone.specifications;

import andreasaderi.capstone.entities.PantryItem;
import andreasaderi.capstone.entities.StorageLocation;
import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.requestDTOs.PantryItemFiltersDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PantryItemSpecification {

    public Specification<PantryItem> specificationPantryItemBuilder(PantryItemFiltersDTO filters, User user) {
        Specification<PantryItem> spec = (root, query, cb) -> cb.conjunction();


        if (user != null) {
            spec = spec.and(hasUser(user));
        }
        
        if (filters == null) {
            return spec;
        }

        if (filters.name() != null && !filters.name().isBlank()) {
            spec = spec.and(hasName(filters.name()));
        }

        if (filters.minQuantity() != null) {
            spec = spec.and(hasQuantityGreaterThan(filters.minQuantity()));
        }

        if (filters.maxQuantity() != null) {
            spec = spec.and(hasQuantityLessThan(filters.maxQuantity()));
        }

        if (filters.minPurchaseDate() != null) {
            spec = spec.and(hasPurchaseDateGreaterThan(filters.minPurchaseDate()));
        }

        if (filters.maxPurchaseDate() != null) {
            spec = spec.and(hasPurchaseDateLessThan(filters.maxPurchaseDate()));
        }

        if (filters.minExpirationDate() != null) {
            spec = spec.and(hasExpirationDateGreaterThan(filters.minExpirationDate()));
        }

        if (filters.maxExpirationDate() != null) {
            spec = spec.and(hasExpirationDateLessThan(filters.maxExpirationDate()));
        }

        if (filters.storageLocation() != null) {
            spec = spec.and(hasStorageLocation(filters.storageLocation()));
        }

        return spec;
    }

    private Specification<PantryItem> hasUser(User user) {
        return (root, query, cb) ->
                cb.equal(root.get("user"), user
                );
    }

    public Specification<PantryItem> hasName(String name) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("ingredientDefinition").get("name")),
                        "%" + name.toLowerCase() + "%"
                );
    }

    public Specification<PantryItem> hasQuantityGreaterThan(Double minQuantity) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("quantity"), minQuantity
                );
    }

    public Specification<PantryItem> hasQuantityLessThan(Double maxQuantity) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("quantity"), maxQuantity
                );
    }

    public Specification<PantryItem> hasPurchaseDateGreaterThan(LocalDate minPurchaseDate) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("purchaseDate"), minPurchaseDate
                );
    }

    public Specification<PantryItem> hasPurchaseDateLessThan(LocalDate maxPurchaseDate) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("purchaseDate"), maxPurchaseDate
                );
    }

    public Specification<PantryItem> hasExpirationDateGreaterThan(LocalDate minExpirationDate) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("expirationDate"), minExpirationDate
                );
    }

    public Specification<PantryItem> hasExpirationDateLessThan(LocalDate maxExpirationDate) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("expirationDate"), maxExpirationDate
                );
    }

    public Specification<PantryItem> hasStorageLocation(StorageLocation storageLocation) {
        return (root, query, cb) ->
                cb.equal(root.get("storageLocation"), storageLocation);
    }
}