package andreasaderi.capstone.tools;

import andreasaderi.capstone.entities.User;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {
    private final String domainName;
    private final String apiKey;

    public EmailSender(@Value("${mailgun.domainName}") String domainName, @Value("${mailgun.apiKey}") String apiKey) {
        this.domainName = domainName;
        this.apiKey = apiKey;
    }

    public void sendCustomRegistrationEmail(User recipient) {
        HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domainName + "/messages")
                .basicAuth("api", this.apiKey)
                .queryString("from", "admin@" + this.domainName)
                .queryString("to", recipient.getEmail())
                .queryString("subject", "Benvenuto sulla piattaforma!")
                .queryString("text", "Ciao, " + recipient.getFirstName() + ".\n\n Ti diamo il benvenuto su questa piattaforma!")
                .asJson();

        System.out.println(response.getBody());
    }

    // metodo pe rinviare email custom esclusiva admin
    public void sendAdminCustomEmail(String recipientEmail, String subject, String text) {
        HttpResponse<JsonNode> response = Unirest.post("https://api.mailgun.net/v3/" + this.domainName + "/messages")
                .basicAuth("api", this.apiKey)
                .queryString("from", "admin@" + this.domainName)
                .queryString("to", recipientEmail)
                .queryString("subject", subject)
                .queryString("text", text)
                .asJson();

        System.out.println(response.getBody());
    }
}
