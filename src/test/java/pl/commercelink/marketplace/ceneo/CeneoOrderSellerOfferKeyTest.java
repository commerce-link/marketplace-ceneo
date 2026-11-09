package pl.commercelink.marketplace.ceneo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.commercelink.marketplace.api.MarketplaceProduct;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CeneoOrderSellerOfferKeyTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void shopProductIdIsReportedAsTheSellerOfferKey() throws Exception {
        // given
        String json = """
                {
                  "Id": "4572c990-c989-4240-8adb-ed1c34d4927b",
                  "DisplayedOrderId": "9859806",
                  "OrderItems": {"results": [
                    {"ShopProductId": "kukfw2t4yz", "Name": "MSI MPG 341CQR QD-OLED X36", "Price": 3499.00, "Count": 1}
                  ]}
                }
                """;

        // when
        MarketplaceProduct product = toProduct(json);

        // then
        assertEquals("kukfw2t4yz", product.sellerOfferKey());
        assertEquals("kukfw2t4yz", product.manufacturerCode());
    }

    @Test
    void lineWithoutShopProductIdHasNoSellerOfferKey() throws Exception {
        // given
        String json = """
                {
                  "Id": "abc",
                  "OrderItems": {"results": [{"Name": "Product", "Price": 10.00, "Count": 1}]}
                }
                """;

        // when
        MarketplaceProduct product = toProduct(json);

        // then
        assertNull(product.sellerOfferKey());
    }

    private static MarketplaceProduct toProduct(String json) throws Exception {
        CeneoOrder order = MAPPER.readValue(json, CeneoOrder.class);
        return new CeneoOrdersImport(null).toMarketplaceOrder(order).products().get(0);
    }
}
