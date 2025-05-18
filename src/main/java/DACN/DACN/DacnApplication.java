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
		SpringApplication.run(DacnApplication.class, args);
	}

}
