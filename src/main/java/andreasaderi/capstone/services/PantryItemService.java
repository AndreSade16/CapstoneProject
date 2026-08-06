package andreasaderi.capstone.services;

import andreasaderi.capstone.entities.IngredientDefinition;
import andreasaderi.capstone.entities.PantryItem;
import andreasaderi.capstone.entities.ShoppingListItem;
import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.exceptions.ConflictException;
import andreasaderi.capstone.exceptions.NotFoundException;
import andreasaderi.capstone.repositories.PantryItemRepository;
import andreasaderi.capstone.requestDTOs.PantryItemDTO;
import andreasaderi.capstone.requestDTOs.PantryItemFiltersDTO;
import andreasaderi.capstone.requestDTOs.PantryItemUpdateDTO;
import andreasaderi.capstone.requestDTOs.PurchasedItemDTO;
import andreasaderi.capstone.specifications.PantryItemSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PantryItemService {

    private final PantryItemRepository pantryItemRepository;
    private final IngredientDefinitionService ingredientDefinitionService;
    private final PantryItemSpecification pantryItemSpecification;

    public PantryItemService(PantryItemRepository pantryItemRepository, IngredientDefinitionService ingredientDefinitionService, PantryItemSpecification pantryItemSpecification) {
        this.pantryItemRepository = pantryItemRepository;
        this.ingredientDefinitionService = ingredientDefinitionService;
        this.pantryItemSpecification = pantryItemSpecification;
    }

    public PantryItem findByIdAndUser(UUID pantryItemId, User user) {
        PantryItem found = findById(pantryItemId);
        if (!found.getUser().getUserId().equals(user.getUserId()))
            throw new ConflictException("This pantry item doesn't belong to this user");

        return found;
    }

    public PantryItem save(PantryItemDTO body, User user) {

        IngredientDefinition ingredientDefinition = ingredientDefinitionService.findById(body.ingredientDefinitionId());
        Optional<PantryItem> mayBePresent = pantryItemRepository.findByIngredientDefinitionAndPurchaseDateAndExpirationDateAndStorageLocationAndUser(ingredientDefinition, body.purchaseDate(), body.expirationDate(), body.storageLocation(), user);
        if (mayBePresent.isPresent()) {
            PantryItem alreadyThere = mayBePresent.get();
            alreadyThere.setQuantity(alreadyThere.getQuantity() + body.quantity());
            return pantryItemRepository.save(alreadyThere);
        }

        return pantryItemRepository.save(new PantryItem(user, ingredientDefinition, body.quantity(), body.purchaseDate(), body.expirationDate(), body.storageLocation()));
    }


    public void createFromPurchasedItem(ShoppingListItem item, PurchasedItemDTO purchasedItem, User authenticatedUser) {
        PantryItemDTO body = new PantryItemDTO(item.getIngredientDefinition().getIngredientDefinitionId(), purchasedItem.purchasedQuantity(), LocalDate.now(), purchasedItem.expirationDate(), purchasedItem.storageLocation());
        this.save(body, authenticatedUser);
    }

    public Page<PantryItem> findAll(int page, int size, String sortBy, Sort.Direction direction, @Valid PantryItemFiltersDTO filters) {
        if (size <= 0) size = 10;
        if (size > 20) size = 20;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Specification<PantryItem> spec = pantryItemSpecification.specificationPantryItemBuilder(filters, null);


        return pantryItemRepository.findAll(spec, pageable);
    }

    public PantryItem findById(UUID pantryItemId) {
        return pantryItemRepository.findById(pantryItemId).orElseThrow(() -> new NotFoundException("Pantry item with id '" + pantryItemId + "' not found"));
    }

    public PantryItem updateOwnPantryItem(UUID pantryItemId, PantryItemUpdateDTO body, User user) {

        PantryItem pantryItem = findByIdAndUser(pantryItemId, user);

        pantryItem.setQuantity(body.quantity());
        pantryItem.setPurchaseDate(body.purchaseDate());
        pantryItem.setExpirationDate(body.expirationDate());
        pantryItem.setStorageLocation(body.storageLocation());

        return pantryItemRepository.save(pantryItem);
    }

    public void deleteOwnItem(UUID pantryItemId, User user) {
        PantryItem pantryItem = findByIdAndUser(pantryItemId, user);
        pantryItemRepository.delete(pantryItem);
    }


    public List<PantryItem> findListByUser(User user) {
        return pantryItemRepository.findListByUser(user);
    }

    public Page<PantryItem> findByUser(User user, int page, int size, String sortBy, Sort.Direction direction) {
        if (size <= 0) size = 10;
        if (size > 20) size = 20;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return pantryItemRepository.findByUser(user, pageable);
    }

    public Page<PantryItem> findByUserWithFilters(User user, int page, int size, String sortBy, Sort.Direction direction, PantryItemFiltersDTO filters) {
        if (size <= 0) size = 10;
        if (size > 20) size = 20;
        if (page < 0) page = 0;
        Specification<PantryItem> spec = pantryItemSpecification.specificationPantryItemBuilder(filters, user);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return pantryItemRepository.findAll(spec, pageable);
    }
}
