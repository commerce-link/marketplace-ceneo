package pl.commercelink.marketplace.ceneo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CeneoOrderDeliveryFormTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void readsTheShopDeliveryFormNameFromTheOrder() throws Exception {
        // given
        String json = """
                {
                  "Id": "593f9b1f-c667-45ac-8c94-0390e7b87414",
                  "DisplayedOrderId": "9859806",
                  "ShopDeliveryFormName": "Poczta Polska, Płatność z góry, List polecony ekonomiczny"
                }
                """;

        // when
        CeneoOrder order = MAPPER.readValue(json, CeneoOrder.class);

        // then
        assertEquals("Poczta Polska, Płatność z góry, List polecony ekonomiczny",
                order.getShopDeliveryFormName());
    }

    @Test
    void orderWithoutADeliveryFormHasNoName() throws Exception {
        // given
        String json = """
                {"Id": "abc"}
                """;

        // when
        CeneoOrder order = MAPPER.readValue(json, CeneoOrder.class);

        // then
        assertNull(order.getShopDeliveryFormName());
    }
}
