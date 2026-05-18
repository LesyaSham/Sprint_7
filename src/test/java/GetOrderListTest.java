import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetOrderListTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru";
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Позитивный тест: проверяет, что при GET-запросе возвращается статус 200 и непустой список заказов")
    public void shouldReturnOrderListInResponseBody() {

        Response response = sendGetRequestOrders();

        checkStatusCode(response, 200);
        checkOrdersListIsNotEmpty(response);
    }

    // =========================================================================
    // ШАГИ С АННОТАЦИЕЙ @Step
    // =========================================================================

    @Step("Отправить GET-запрос на получение списка заказов /api/v1/orders")
    public Response sendGetRequestOrders() {
        return given()
                .when()
                .get("/api/v1/orders");
    }

    @Step("Проверить статус-код ответа: ожидается {expectedCode}")
    public void checkStatusCode(Response response, int expectedCode) {
        assertEquals(expectedCode, response.statusCode(), "Статус-код ответа некорректен");
    }

    @Step("Проверить, что в теле ответа возвращается объект orders, и он не пустой")
    public void checkOrdersListIsNotEmpty (Response response) {
        response.then().assertThat()
                .body("orders", notNullValue());
    }

}
