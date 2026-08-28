package andreasaderi.capstone.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pantry_items")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"user"})
public class PantryItem {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, name = "pantry_item_id")
    private UUID pantryItemId;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "ingredient_definition_id")
    private IngredientDefinition ingredientDefinition;
    @Column(nullable = false)
    private double quantity;
    @Column(nullable = false, name = "purchase_date")
    private LocalDate purchaseDate;
    @Column(nullable = false, name = "expiration_date")
    private LocalDate expirationDate;
    @Column(nullable = false, name = "storage_location")
    @Enumerated(EnumType.STRING)
    private StorageLocation storageLocation;

    public PantryItem(User user, IngredientDefinition ingredientDefinition, double quantity, LocalDate purchaseDate, LocalDate expirationDate, StorageLocation storageLocation) {
        this.user = user;
        this.ingredientDefinition = ingredientDefinition;
        this.quantity = quantity;
        this.purchaseDate = purchaseDate;
        this.expirationDate = expirationDate;
        this.storageLocation = storageLocation;
    }
}
