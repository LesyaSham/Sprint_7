import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    @BeforeAll
    public static void globalSetUp() {
        // Устанавливаем базовый URL один раз для всего проекта перед запуском любых тестов
        RestAssured.baseURI = "https://qa-scooter.education-services.ru";
    }
}