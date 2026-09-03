package andreasaderi.capstone.tools;


import andreasaderi.capstone.entities.User;
import andreasaderi.capstone.requestDTOs.BrevoEmailRequestDTO;
import andreasaderi.capstone.requestDTOs.BrevoRecipientDTO;
import andreasaderi.capstone.requestDTOs.BrevoSenderDTO;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailSender {
    private final String apiKey;
    private final String senderEmail;
    private final String senderName;
    private final String frontendUrl;

    public EmailSender(
            @Value("${brevo.apiKey}") String apiKey,
            @Value("${brevo.senderEmail}") String senderEmail,
            @Value("${brevo.senderName}") String senderName,
            @Value("${frontend.url}") String frontendUrl
    ) {
        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.frontendUrl = frontendUrl;
    }

    public void sendCustomRegistrationEmail(User recipient) {
        String subject = "Welcome to Fresko!";
        String loginUrl = frontendUrl + "/login";

        String text = "Hi, " + recipient.getFirstName() + ".\n\n"
                + "We welcome you to Fresko!\n\n"
                + "Stop wasting your food with us!\n\n"
                + "Log in here: " + loginUrl;

        String html = """
                <html>
                          <body style="margin: 0; padding: 0; background-color: #061809; font-family: Arial, Helvetica, sans-serif;">
                            <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="background-color: #061809; padding: 40px 20px;">
                              <tr>
                                <td align="center">
                                  <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="max-width: 480px; background-color: #112615; border-radius: 12px; padding: 40px 30px;">
                                    <tr>
                                      <td align="center" style="padding-bottom: 20px;">
                                        <h1 style="color: #F2F2E9; font-size: 24px; margin: 0;">Welcome to <span style="color: #BADB06;">Fresko</span>, %s!</h1>
                                      </td>
                                    </tr>
                                    <tr>
                                      <td style="color: #F2F2E9; font-size: 16px; line-height: 1.6; padding-bottom: 30px;">
                                        <p style="margin: 0 0 12px 0;">We welcome you to Fresko!</p>
                                        <p style="margin: 0;">Stop wasting your food with us!</p>
                                      </td>
                                    </tr>
                                    <tr>
                                      <td align="center">
                                        <a href="%s" style="display: inline-block; background-color: #BADB06; color: #061809; text-decoration: none; font-weight: bold; font-size: 16px; padding: 12px 32px; border-radius: 8px; border: 1px solid #000000;">
                                          Log in to your account
                                        </a>
                                      </td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                            </table>
                          </body>
                        </html>
                """.formatted(recipient.getFirstName(), loginUrl);

        BrevoRecipientDTO to = new BrevoRecipientDTO(recipient.getEmail(), recipient.getFirstName());
        sendEmail(to, subject, text, html);
    }

    public void sendAdminCustomEmail(String recipientEmail, String subject, String text, String html) {
        BrevoRecipientDTO to = new BrevoRecipientDTO(recipientEmail);
        sendEmail(to, subject, text, html);
    }

    private void sendEmail(BrevoRecipientDTO recipient, String subject, String text, String html) {
        BrevoEmailRequestDTO requestBody = new BrevoEmailRequestDTO(
                new BrevoSenderDTO(senderName, senderEmail),
                List.of(recipient),
                subject,
                text,
                html
        );

        HttpResponse<JsonNode> response = Unirest.post("https://api.brevo.com/v3/smtp/email")
                .header("accept", "application/json")
                .header("api-key", apiKey)
                .header("content-type", "application/json")
                .body(requestBody)
                .asJson();

        if (response.getStatus() >= 400) {
            throw new RuntimeException("An error occurred while sending a Brevo email: " + response.getBody());
        }

        System.out.println(response.getBody());
    }
}