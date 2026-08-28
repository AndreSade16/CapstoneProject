package andreasaderi.capstone.requestDTOs;

import andreasaderi.capstone.entities.Role;

public record UserFiltersDTO(
        String username,
        String email,
        String firstName,
        String lastName,
        Role role
) {
}
