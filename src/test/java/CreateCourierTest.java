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

public class CreateCourierTest extends BaseTest {

    private Integer courierId;
    private String uniqueLogin;
    private CourierSteps courierSteps;

    @BeforeEach
    public void setUp() {
        courierSteps = new CourierSteps();
        uniqueLogin = "user_" + new Random().nextInt(100000);
        courierId = null;
    }

    @AfterEach
    public void tearDown() {
        if (courierId != null) {
            courierSteps.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Курьера можно создавать + запрос возвращает правильный код ответа + успешный запрос возвращает ok: true")
    public void courierCanBeCreated() {
        Courier courier = new Courier(uniqueLogin, "pass1234", "Sanya");

        Response response = courierSteps.createCourier(courier);

        courierSteps.checkStatusCode(response, 201);
        courierSteps.checkResponseBodyOkField(response, true);

        CourierCreds creds = new CourierCreds(uniqueLogin, "pass1234");
        Response loginResponse = courierSteps.loginCourier(creds);
        if (loginResponse.statusCode() == 200) {
            courierId = loginResponse.body().as(CourierLoginResponse.class).getId();
        }
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров + если создать пользователя с логином, который уже есть, возвращается ошибка.")
    public void cannotCreateTwoIdenticalCouriers() {
        Courier courier = new Courier(uniqueLogin, "pass1234", "Sanya");

        Response firstResponse = courierSteps.createCourier(courier);
        courierSteps.checkStatusCode(firstResponse, 201);

        CourierCreds creds = new CourierCreds(uniqueLogin, "pass1234");
        Response loginResponse = courierSteps.loginCourier(creds);
        if (loginResponse.statusCode() == 200) {
            courierId = loginResponse.body().as(CourierLoginResponse.class).getId();
        }

        Response secondResponse = courierSteps.createCourier(courier);

        courierSteps.checkStatusCode(secondResponse, 409);
        courierSteps.checkResponseBodyMessageField(secondResponse, "Этот логин уже используется. Попробуйте другой.");
    }

    @Test
    @DisplayName("Нельзя создать курьера без поля login + если одного из полей нет, запрос возвращает ошибку")
    public void cannotCreateCourierWithoutLogin() {
        Courier courierWithoutLogin = new Courier("", "pass1234", "Sanya");

        Response response = courierSteps.createCourier(courierWithoutLogin);

        courierSteps.checkStatusCode(response, 400);
        courierSteps.checkResponseBodyMessageField(response, "Недостаточно данных для создания учетной записи");
    }

    @Test
    @DisplayName("Нельзя создать курьера без поля password  + если одного из полей нет, запрос возвращает ошибку")
    public void cannotCreateCourierWithoutPassword() {
        Courier courierWithoutPassword = new Courier(uniqueLogin, "", "Sanya");

        Response response = courierSteps.createCourier(courierWithoutPassword);

        courierSteps.checkStatusCode(response, 400);
        courierSteps.checkResponseBodyMessageField(response, "Недостаточно данных для создания учетной записи");
    }
}
