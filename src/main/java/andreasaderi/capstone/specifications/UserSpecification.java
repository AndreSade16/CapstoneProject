package andreasaderi.capstone.specifications;

import andreasaderi.capstone.entities.Role;
import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.requestDTOs.UserFiltersDTO;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecification {

    public Specification<User> specificationUserBuilder(UserFiltersDTO filters) {
        Specification<User> spec = (root, query, cb) -> cb.conjunction();

        if (filters.username() != null && !filters.username().isBlank()) {
            spec = spec.and(hasUsername(filters.username()));
        }

        if (filters.email() != null && !filters.email().isBlank()) {
            spec = spec.and(hasEmail(filters.email()));
        }

        if (filters.firstName() != null && !filters.firstName().isBlank()) {
            spec = spec.and(hasFirstName(filters.firstName()));
        }

        if (filters.lastName() != null && !filters.lastName().isBlank()) {
            spec = spec.and(hasLastName(filters.lastName()));
        }

        if (filters.role() != null) {
            spec = spec.and(hasRole(filters.role()));
        }

        return spec;
    }

    public Specification<User> hasUsername(String username) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("username")),
                        "%" + username.toLowerCase() + "%"
                );
    }


    public Specification<User> hasEmail(String email) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("email")),
                        "%" + email.toLowerCase() + "%"
                );
    }

    public Specification<User> hasFirstName(String firstName) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("firstName")),
                        "%" + firstName.toLowerCase() + "%"
                );
    }

    public Specification<User> hasLastName(String lastName) {
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("lastName")),
                        "%" + lastName.toLowerCase() + "%"
                );
    }

    public Specification<User> hasRole(Role role) {
        return (root, query, cb) ->
                cb.equal(root.get("role"), role);
    }

}
