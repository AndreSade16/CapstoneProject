package andreasaderi.capstone.repositories;

import andreasaderi.capstone.entities.IngredientDefinition;
import andreasaderi.capstone.entities.PantryItem;
import andreasaderi.capstone.entities.StorageLocation;
import andreasaderi.capstone.entities.User;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PantryItemRepository extends JpaRepository<PantryItem, UUID>, JpaSpecificationExecutor<PantryItem> {

    Optional<PantryItem> findByIngredientDefinitionAndPurchaseDateAndExpirationDateAndStorageLocationAndUser(IngredientDefinition ingredientDefinition, @PastOrPresent(message = "Purchase date must be in the present or in the past") @NotNull(message = "Purchase date can't be null") LocalDate localDate, @FutureOrPresent(message = "Expiration date must be in the future") @NotNull(message = "Expiration date can't be null") LocalDate localDate1, @NotNull(message = "Storage location can't be null") StorageLocation storageLocation, User user);
}