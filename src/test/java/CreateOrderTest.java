import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

    public class CreateOrderTest {

        @BeforeEach
        public void setUp() {
            RestAssured.baseURI = "https://qa-scooter.education-services.ru";
        }

        private static Stream<List<String>> provideColors() {
            return Stream.of(
                    List.of("BLACK"),
                    List.of("GREY"),
                    List.of("BLACK", "GREY"),
                    List.of()
            );
        }

        @ParameterizedTest(name = "Цвета самоката: {0}")
        @MethodSource("provideColors")
        @DisplayName("Проверяем, что при любых цветах заказ создается, возвращает код 201 и track-номер")
        public void orderCanBeCreatedWithDifferentColors(List<String> selectedColors) {
            Order order = new Order(
                    "Naruto", "Uchiha", "Konoha, 142 apt.", "Черкизовская",
                    "+7 999 111 22 33", 2, "2026-06-01", "Жду у главных ворот",
                    selectedColors
            );

            Response response = sendPostRequestCreateOrder(order);
            checkStatusCode(response, 201);
            checkResponseBodyHasTrack(response);
        }

        // =========================================================================
        // ШАГИ С АННОТАЦИЕЙ @Step
        // =========================================================================

        @Step("Отправить POST-запрос на создание заказа /api/v1/orders")
        public Response sendPostRequestCreateOrder(Order order) {
            return given()
                    .contentType(ContentType.JSON)
                    .body(order)
                    .when()
                    .post("/api/v1/orders");
        }

        @Step("Проверить статус-код ответа: ожидается {expectedCode}")
        public void checkStatusCode(Response response, int expectedCode) {
            assertEquals(expectedCode, response.statusCode());
        }
        @Step("Проверить, что тело ответа содержит номер отслеживания 'track'")
        public void checkResponseBodyHasTrack(Response response) {
            response.then().assertThat().body("track", notNullValue());
        }
    }

