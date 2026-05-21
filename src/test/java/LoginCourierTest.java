import io.restassured.response.Response;
import models.Courier;
import models.CourierCreds;
import models.CourierLoginResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.CourierSteps;

import java.util.Random;

public class LoginCourierTest extends BaseTest {

    private Integer courierId;
    private String registeredLogin;
    private String registeredPassword;
    private CourierSteps courierSteps;

    @BeforeEach
    public void setUp() {
        courierSteps = new CourierSteps();
        registeredLogin = "login" + new Random().nextInt(100000);
        registeredPassword = "password12345";

        Courier courier = new Courier(registeredLogin, registeredPassword, "Oleg");
        courierSteps.createCourier(courier);
    }

    @AfterEach
    public void tearDown() {
        if (courierId != null) {
            courierSteps.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Курьер может авторизоваться + успешный вход возвращает статус 200 и числовой id")
    public void courierCanSuccessfullyLogin() {
        CourierCreds courierCreds = new CourierCreds(registeredLogin, registeredPassword);

        Response response = courierSteps.loginCourier(courierCreds);

        courierSteps.checkStatusCode(response, 200);
        courierSteps.checkResponseBodyHasId(response);

        courierId = response.body().as(CourierLoginResponse.class).getId();
    }

    @Test
    @DisplayName("Система возвращает ошибку, если неправильно указать логин")
    public void loginWithWrongUsernameReturnsError() {
        CourierCreds wrongLogin = new CourierCreds("wrong_login", registeredPassword);

        Response response = courierSteps.loginCourier(wrongLogin);

        courierSteps.checkStatusCode(response, 404);
        courierSteps.checkResponseBodyMessageField(response, "Учетная запись не найдена");
    }

    @Test
    @DisplayName("Система возвращает ошибку, если неправильно указать пароль")
    public void loginWithWrongPasswordReturnsError() {
        CourierCreds wrongPassword = new CourierCreds(registeredLogin, "wrong_pass");

        Response response = courierSteps.loginCourier(wrongPassword);

        courierSteps.checkStatusCode(response, 404);
        courierSteps.checkResponseBodyMessageField(response, "Учетная запись не найдена");
    }

    @Test
    @DisplayName("Для авторизации нужно передать все обязательные поля + если какого-то поля нет, запрос возвращает ошибку")
    public void cannotLoginWithoutPasswordField() {
        CourierCreds credsWithoutPassword = new CourierCreds(registeredLogin, "");

        Response response = courierSteps.loginCourier(credsWithoutPassword);

        courierSteps.checkStatusCode(response, 400);
        courierSteps.checkResponseBodyMessageField(response, "Недостаточно данных для входа");
    }

    @Test
    @DisplayName("Если авторизоваться под несуществующим пользователем, возвращается ошибка")
    public void loginAsNonExistentUserReturnsError() {
        CourierCreds nonExistentUser = new CourierCreds("ghost_courier_999999", "pass123");

        Response response = courierSteps.loginCourier(nonExistentUser);

        courierSteps.checkStatusCode(response, 404);
        courierSteps.checkResponseBodyMessageField(response, "Учетная запись не найдена");
    }
}