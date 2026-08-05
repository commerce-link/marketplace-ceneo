package pl.commercelink.marketplace.ceneo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CeneoPickupPointWireFormatTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void readsThePointCodeFromTheNameField() throws Exception {
        // given
        String json = """
                {
                  "OrderId": "593f9b1f-c667-45ac-8c94-0390e7b87414",
                  "SubOrderId": 9859806,
                  "ShopGuid": "234f642b-9a8c-4fcd-a032-c6e9eebd4000",
                  "Name": "WRO34N",
                  "StreetAddress": "Legnicka 48",
                  "City": "Wrocław",
                  "PostCode": "54-202"
                }
                """;

        // when
        CeneoPickupPoint point = MAPPER.readValue(json, CeneoPickupPoint.class);

        // then
        assertEquals("WRO34N", point.getCode());
        assertEquals("Legnicka 48", point.getStreetAddress());
        assertEquals("Wrocław", point.getCity());
        assertEquals("54-202", point.getPostCode());
    }

    @Test
    void orderWithoutPickupPointCollectionHasNoPoint() throws Exception {
        // given
        String json = """
                {"Id": "abc", "PickupPoint": {"results": []}}
                """;

        // when
        CeneoOrder order = MAPPER.readValue(json, CeneoOrder.class);

        // then
        assertNull(order.getPickupPoint());
    }

    @Test
    void expandedPickupPointIsReadFromTheOrder() throws Exception {
        // given
        String json = """
                {
                  "Id": "abc",
                  "PickupPoint": {"results": [{"Name": "WRO117M", "StreetAddress": "Rumiankowa 74",
                                               "City": "Wroclaw", "PostCode": "11-111"}]}
                }
                """;

        // when
        CeneoOrder order = MAPPER.readValue(json, CeneoOrder.class);

        // then
        assertEquals("WRO117M", order.getPickupPoint().getCode());
        assertEquals("Rumiankowa 74", order.getPickupPoint().getStreetAddress());
    }
}
