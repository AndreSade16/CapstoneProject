package andreasaderi.capstone.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class Config {

    @Bean
    public Cloudinary getCloudinaryUploader(@Value("${cloudinary.apikey}") String apiKey, @Value("${cloudinary.username}") String cloudName, @Value("${cloudinary.secret}") String cloudSecret) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", cloudSecret);

        return new Cloudinary(config);
    }
}