package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.ShoppingList;
import andreasaderi.capstone.entities.ShoppingListStatus;
import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.ConflictException;
import andreasaderi.capstone.repositories.ShoppingListRepository;
import org.springframework.stereotype.Service;

@Service
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;


    public ShoppingListService(ShoppingListRepository shoppingListRepository) {
        this.shoppingListRepository = shoppingListRepository;
    }


    public ShoppingList save(User authenticatedUser) {
        if (shoppingListRepository.existsByUserAndStatus(authenticatedUser, ShoppingListStatus.ACTIVE))
            throw new ConflictException("You already have an active Shopping List. Complete it or edit that one");
        return shoppingListRepository.save(new ShoppingList(authenticatedUser));
    }


}
