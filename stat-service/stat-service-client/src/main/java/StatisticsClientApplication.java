import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.practicum.client")
public class StatisticsClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(StatisticsClientApplication.class, args);
    }
}
