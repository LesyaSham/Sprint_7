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
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginCourierTest {

    private Integer courierId;
    private String registeredLogin;
    private String registeredPassword;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru";

        registeredLogin = "login" + new Random().nextInt(100000);
        registeredPassword = "password12345";

        Courier courier = new Courier(registeredLogin, registeredPassword, "Oleg");
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .post("/api/v1/courier");
    }

    @AfterEach
    public void tearDown() {
        if (courierId != null) {
            sendDeleteRequest(courierId);
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться + успешный вход возвращает статус 200 и числовой id")
    public void courierCanSuccessfullyLogin() {
        CourierCreds courierCreds = new CourierCreds(registeredLogin, registeredPassword);

        Response response = sendPostRequestLogin(courierCreds);

        checkStatusCode(response, 200);
        checkResponseBodyHasId(response);

        courierId = response.body().as(CourierLoginResponse.class).getId();
    }

    @Test
    @DisplayName("Система возвращает ошибку, если неправильно указать логин")
    public void loginWithWrongUsernameReturnsError() {
        CourierCreds wrongLogin = new CourierCreds("wrong_login", registeredPassword);

        Response response = sendPostRequestLogin(wrongLogin);

        checkStatusCode(response, 404);
        checkResponseBodyMessageField(response, "Учетная запись не найдена");
    }

    @Test
    @DisplayName("Система возвращает ошибку, если неправильно указать пароль")
    public void loginWithWrongPasswordReturnsError() {
        CourierCreds wrongPassword = new CourierCreds(registeredLogin, "wrong_pass");

        Response response = sendPostRequestLogin(wrongPassword);

        checkStatusCode(response, 404);
        checkResponseBodyMessageField(response, "Учетная запись не найдена");
    }

    @Test
    @DisplayName("Для авторизации нужно передать все обязательные поля + если какого-то поля нет, запрос возвращает ошибку")
    public void cannotLoginWithoutPasswordField() {
        CourierCreds credsWithoutPassword = new CourierCreds(registeredLogin, "");

        Response response = sendPostRequestLogin(credsWithoutPassword);

        checkStatusCode(response, 400);
        checkResponseBodyMessageField(response, "Недостаточно данных для входа");
    }

    @Test
    @DisplayName("Если авторизоваться под несуществующим пользователем, возвращается ошибка")
    public void loginAsNonExistentUserReturnsError() {
        CourierCreds nonExistentUser = new CourierCreds("ghost_courier_999999", "pass123");

        Response response = sendPostRequestLogin(nonExistentUser);

        checkStatusCode(response, 404);
        checkResponseBodyMessageField(response, "Учетная запись не найдена");
    }

    // =========================================================================
    // ШАГИ С АННОТАЦИЕЙ @Step
    // =========================================================================

    @Step("Отправить POST-запрос на авторизацию /api/v1/courier/login")
    public Response sendPostRequestLogin(CourierCreds courierCreds) {
        return given()
                .contentType(ContentType.JSON)
                .body(courierCreds)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Проверить статус-код ответа: ожидается {expectedCode}")
    public void checkStatusCode(Response response, int expectedCode) {
        assertEquals(expectedCode, response.statusCode(), "Статус-код ответа некорректен");
    }

    @Step("Проверить, что в ответе авторизации вернулся числовой ID курьера")
    public void checkResponseBodyHasId(Response response) {
        response.then().assertThat().body("id", notNullValue());
    }

    @Step("Проверить сообщение об ошибке: ожидается '{expectedMessage}'")
    public void checkResponseBodyMessageField(Response response, String expectedMessage) {
        response.then().assertThat().body("message", equalTo(expectedMessage));
    }

    private void sendDeleteRequest(int id) {
        given()
                .when()
                .delete("/api/v1/courier/" + id);
    }
}
