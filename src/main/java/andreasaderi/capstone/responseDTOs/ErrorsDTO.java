package andreasaderi.capstone.responseDTOs;

import java.time.LocalDateTime;

public record ErrorsDTO(String message, LocalDateTime time) {
}
