package DACN.DACN;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DacnApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load();
		System.setProperty("groq.api.key", dotenv.get("GROQ_API_KEY"));
		System.setProperty("groq.api.url", dotenv.get("GROQ_API_URL"));
		System.setProperty("spring.security.oauth2.client.registration.google.client-id", dotenv.get("OAUTH_CLIENT_ID"));
		System.setProperty("spring.security.oauth2.client.registration.google.client-secret", dotenv.get("OAUTH_SECRET_KEY"));

		SpringApplication.run(DacnApplication.class, args);
	}

}
