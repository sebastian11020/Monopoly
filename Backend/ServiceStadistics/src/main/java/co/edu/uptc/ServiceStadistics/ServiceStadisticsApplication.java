package co.edu.uptc.ServiceStadistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServiceStadisticsApplication {

	public static void main(String[] args) {
		System.out.println("DB_URL from ENV: " + System.getenv("DB_URL"));
		SpringApplication.run(ServiceStadisticsApplication.class, args);
	}

}
