package andreasaderi.capstone.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
//Aggiunge il constraint di UNIQUE in maniera tale che ci sia solo un ingrediente con lo stesso nome per lista della spesa
@Table(name = "shopping_list_items", uniqueConstraints = @UniqueConstraint(columnNames = {"shopping_list_id", "ingredient_definition_id"}
))
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"shoppingList"})
public class ShoppingListItem {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, name = "shopping_list_item_id")
    private UUID shoppingListItemId;
    @ManyToOne
    @JoinColumn(nullable = false, name = "shopping_list_id")
    private ShoppingList shoppingList;
    @ManyToOne
    @JoinColumn(nullable = false, name = "ingredient_definition_id")
    private IngredientDefinition ingredientDefinition;
    @Column(name = "suggested_unit")
    @Enumerated(EnumType.STRING)
    private Unit suggestedUnit;
    @Column(name = "suggested_quantity")
    private Double suggestedQuantity;
    @Column(name = "purchased_quantity")
    private Double purchasedQuantity;

    public ShoppingListItem(ShoppingList shoppingList, IngredientDefinition ingredientDefinition, Double suggestedQuantity, Unit suggestedUnit) {
        this.shoppingList = shoppingList;
        this.ingredientDefinition = ingredientDefinition;
        this.suggestedQuantity = suggestedQuantity;
        this.suggestedUnit = suggestedUnit;
        this.purchasedQuantity = null;
    }
}
