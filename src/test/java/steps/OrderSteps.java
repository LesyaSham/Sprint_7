package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Order;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderSteps {

    @Step("Отправить POST-запрос на создание заказа /api/v1/orders")
    public Response createOrder(Order order) {
        return given()
                .contentType(ContentType.JSON)
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    @Step("Отправить GET-запрос на получение списка заказов /api/v1/orders")
    public Response getOrdersList() {
        return given()
                .when()
                .get("/api/v1/orders");
    }

    @Step("Проверить статус-код ответа: ожидается {expectedCode}")
    public void checkStatusCode(Response response, int expectedCode) {
        assertEquals(expectedCode, response.statusCode(), "Статус-код ответа некорректен");
    }

    @Step("Проверить, что тело ответа содержит номер отслеживания 'track'")
    public void checkResponseBodyHasTrack(Response response) {
        response.then().assertThat().body("track", notNullValue());
    }

    @Step("Проверить, что в теле ответа возвращается именно список заказов")
    public void checkOrdersIsList(Response response) {
        response.then().assertThat().body("orders", instanceOf(List.class));
    }
}
