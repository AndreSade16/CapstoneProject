package andreasaderi.capstone.requestDTOs;

import java.util.List;

public record BrevoEmailRequestDTO(
        BrevoSenderDTO sender,
        List<BrevoRecipientDTO> to,
        String subject,
        String textContent,
        String htmlContent
) {
}