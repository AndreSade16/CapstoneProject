package andreasaderi.capstone.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "ingredient_definitions")
@Getter
@Setter
@NoArgsConstructor
public class IngredientDefinition {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, name = "ingredient_definition_id")
    private UUID ingredientDefinitionId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false, name = "image_url")
    private String imageUrl;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;
    @Column(nullable = false, name = "quantity_type")
    @Enumerated(EnumType.STRING)
    private QuantityType quantityType;
    @Column(nullable = false, name = "default_storage_location")
    @Enumerated(EnumType.STRING)
    private StorageLocation defaultStorageLocation;
    @Column(nullable = false, name = "shelf_life_days")
    private int shelfLifeDays;
    @Column(name = "alternative_usages")
    private String alternativeUsages;
    @Column(nullable = false)
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<Season> seasonality = new HashSet<>();

    public IngredientDefinition(String name, String description, String imageUrl, Category category, QuantityType quantityType, StorageLocation defaultStorageLocation, int shelfLifeDays, String alternativeUsages, Set<Season> seasonality) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.quantityType = quantityType;
        this.defaultStorageLocation = defaultStorageLocation;
        this.shelfLifeDays = shelfLifeDays;
        this.alternativeUsages = alternativeUsages;
        this.seasonality = seasonality;
    }
}
