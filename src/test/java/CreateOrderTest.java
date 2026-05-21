import io.restassured.response.Response;
import models.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import steps.OrderSteps;

import java.util.List;
import java.util.stream.Stream;

    public class CreateOrderTest extends BaseTest {
        private OrderSteps orderSteps;

        @BeforeEach
        public void setUp() {
            orderSteps = new OrderSteps();
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

            Response response = orderSteps.createOrder(order);
            orderSteps.checkStatusCode(response, 201);
            orderSteps.checkResponseBodyHasTrack(response);
        }
    }

