package andreasaderi.capstone.requestDTOs;

public record BrevoRecipientDTO(String email, String name) {

    public BrevoRecipientDTO(String email) {
        this(email, null);
    }
}
