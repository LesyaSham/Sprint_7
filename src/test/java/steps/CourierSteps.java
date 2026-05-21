package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Courier;
import models.CourierCreds;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CourierSteps {
    @Step("Отправить POST-запрос на создание курьера /api/v1/courier")
    public Response createCourier(Courier courier) {
        return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Отправить POST-запрос на авторизацию /api/v1/courier/login")
    public Response loginCourier(CourierCreds courierCreds) {
        return given()
                .contentType(ContentType.JSON)
                .body(courierCreds)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Отправить DELETE-запрос на удаление курьера с ID: {id}")
    public Response deleteCourier(int id) {
        return given()
                .when()
                .delete("/api/v1/courier/" + id);
    }

    @Step("Проверить статус-код ответа: ожидается {expectedCode}")
    public void checkStatusCode(Response response, int expectedCode) {
        assertEquals(expectedCode, response.statusCode(), "Статус-код ответа некорректен");
    }

    @Step("Проверить, что поле 'ok' в ответе равно {expectedValue}")
    public void checkResponseBodyOkField(Response response, boolean expectedValue) {
        response.then().assertThat().body("ok", equalTo(expectedValue));
    }

    @Step("Проверить сообщение об ошибке: ожидается '{expectedMessage}'")
    public void checkResponseBodyMessageField(Response response, String expectedMessage) {
        response.then().assertThat().body("message", equalTo(expectedMessage));
    }

    @Step("Проверить, что в ответе авторизации вернулся числовой ID курьера")
    public void checkResponseBodyHasId(Response response) {
        response.then().assertThat().body("id", notNullValue());
    }
}
