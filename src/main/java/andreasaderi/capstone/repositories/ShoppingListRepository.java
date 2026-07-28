package andreasaderi.capstone.repositories;

import andreasaderi.capstone.entities.ShoppingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingList, UUID>, JpaSpecificationExecutor<ShoppingList> {

}