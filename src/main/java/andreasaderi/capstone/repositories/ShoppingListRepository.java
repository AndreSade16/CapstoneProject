package andreasaderi.capstone.repositories;

import andreasaderi.capstone.entities.ShoppingList;
import andreasaderi.capstone.entities.ShoppingListStatus;
import andreasaderi.capstone.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, UUID>, JpaSpecificationExecutor<ShoppingList> {
    boolean existsByUserAndShoppingListStatus(User user, ShoppingListStatus shoppingListStatus);
}