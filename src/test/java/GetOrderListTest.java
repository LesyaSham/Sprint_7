import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import steps.OrderSteps;

public class GetOrderListTest extends BaseTest {
    private OrderSteps orderSteps;

    @BeforeEach
    public void setUp() {
        orderSteps = new OrderSteps();
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Позитивный тест: проверяет, что при GET-запросе возвращается статус 200 и непустой список заказов")
    public void shouldReturnOrderListInResponseBody() {

        Response response = orderSteps.getOrdersList();

        orderSteps.checkStatusCode(response, 200);
        orderSteps.checkOrdersIsList(response);
    }
}
