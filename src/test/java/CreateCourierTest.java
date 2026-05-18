import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Courier;
import models.CourierCreds;
import models.CourierLoginResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateCourierTest {

    private Integer courierId;
    private String uniqueLogin;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru";

        uniqueLogin = "user_" + new Random().nextInt(100000);
        courierId = null;
    }

    @AfterEach
    public void tearDown() {
        if (courierId != null) {
            sendDeleteRequest(courierId);
        }
    }

    @Test
    @DisplayName("Курьера можно создавать + запрос возвращает правильный код ответа + успешный запрос возвращает ok: true")
    public void courierCanBeCreated() {

        Courier courier = new Courier(uniqueLogin, "pass1234", "Sanya");

        Response response = sendPostRequestCreate(courier);

        checkStatusCode(response, 201);
        checkResponseBodyOkField(response, true);

        saveCourierIdForTearDown(uniqueLogin, "pass1234");
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров + если создать пользователя с логином, который уже есть, возвращается ошибка.")
    public void cannotCreateTwoIdenticalCouriers() {
        Courier courier = new Courier(uniqueLogin, "pass1234", "Sanya");

        Response firstResponse = sendPostRequestCreate(courier);
        checkStatusCode(firstResponse, 201);

        saveCourierIdForTearDown(uniqueLogin, "pass1234");

        Response secondResponse = sendPostRequestCreate(courier);

        checkStatusCode(secondResponse, 409);
        checkResponseBodyMessageField(secondResponse, "Этот логин уже используется. Попробуйте другой.");
    }

    @Test
    @DisplayName("Нельзя создать курьера без поля login + если одного из полей нет, запрос возвращает ошибку")
    public void cannotCreateCourierWithoutLogin() {
        Courier courierWithoutLogin = new Courier("", "pass1234", "Sanya");

        Response response = sendPostRequestCreate(courierWithoutLogin);

        checkStatusCode(response, 400);
        checkResponseBodyMessageField(response, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @DisplayName("Нельзя создать курьера без поля password  + если одного из полей нет, запрос возвращает ошибку")
    public void cannotCreateCourierWithoutPassword() {
        Courier courierWithoutPassword = new Courier(uniqueLogin, "", "Sanya");

        Response response = sendPostRequestCreate(courierWithoutPassword);

        checkStatusCode(response, 400);
        checkResponseBodyMessageField(response, "Недостаточно данных для создания учетной записи");
    }

    // =========================================================================
    // ШАГИ С АННОТАЦИЕЙ @Step
    // =========================================================================

    @Step("Отправить POST-запрос на создание курьера")
    public Response sendPostRequestCreate(Courier courier) {
        return given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Проверить статус-код ответа: ожидается {expectedCode}")
    public void checkStatusCode(Response response, int expectedCode) {
        assertEquals(expectedCode, response.statusCode());
    }

    @Step("Проверить, что поле 'ok' в ответе равно {expectedValue}")
    public void checkResponseBodyOkField(Response response, boolean expectedValue) {
        response.then().assertThat().body("ok", equalTo(expectedValue));
    }

    @Step("Проверить сообщение об ошибке: ожидается '{expectedMessage}'")
    public void checkResponseBodyMessageField(Response response, String expectedMessage) {
        response.then().assertThat().body("message", equalTo(expectedMessage));
    }

    //Десериализация (для удаления данных после теста)
    private void saveCourierIdForTearDown(String login, String password) {
        CourierCreds creds = new CourierCreds(login, password);

        Response loginResponse = given()
                .contentType(ContentType.JSON)
                .body(creds)
                .post("/api/v1/courier/login");

        if (loginResponse.statusCode() == 200) {
            CourierLoginResponse responseBody = loginResponse.body().as(CourierLoginResponse.class);
            courierId = responseBody.getId();
        }
    }

    private void sendDeleteRequest(int id) {
        given()
                .when()
                .delete("/api/v1/courier/" + id);
    }
}
