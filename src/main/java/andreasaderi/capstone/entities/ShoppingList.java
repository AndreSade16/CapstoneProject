package andreasaderi.capstone.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shopping_lists")
@Getter
@Setter
@NoArgsConstructor
public class ShoppingList {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(nullable = false, name = "shopping_list_id")
    private UUID shoppingListId;
    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    @Column(nullable = false, name = "created_at")
    private LocalDate createdAt;
    @Column(nullable = false, name = "updated_at")
    private LocalDate updatedAt;
    @Column(nullable = false, name = "shopping_list_status")
    @Enumerated(EnumType.STRING)
    private ShoppingListStatus shoppingListStatus;

    @OneToMany(
            mappedBy = "shoppingList",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ShoppingListItem> items = new ArrayList<>();

    public ShoppingList(User user) {
        this.user = user;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.shoppingListStatus = ShoppingListStatus.ACTIVE;
    }
}
