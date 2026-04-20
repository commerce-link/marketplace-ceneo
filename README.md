# Marketplace Ceneo

[Ceneo.pl](https://www.ceneo.pl) "Kup Teraz" marketplace integration for the CommerceLink platform. Implements the [marketplace-api](../marketplace-api) provider interface with support for **order import and order lifecycle updates** through the Ceneo WebApi.

This module intentionally does **not** depend on the shared `rest-client` library used by sibling modules (`marketplace-empik`, `marketplace-morele`). Ceneo's WebApi diverges from the conventions assumed there: mutating operations are GET with query parameters, and two parallel authentication schemes coexist (Bearer for BasketService, apiKey-in-query for legacy v2 function endpoints). Wrapping those quirks as generic knobs on the shared client would leak Ceneo-specific concerns into the framework, so this module ships its own thin HTTP helper built on `java.net.http.HttpClient` + Jackson.

## Ceneo API Overview

- **AuthorizationService** (`/AuthorizationService.svc/GetToken`) — exchanges the API key (sent as `Authorization: Basic <apiKey>`) for a short-lived Bearer token. The token is returned in the `access_token` response header (HTTP 204, empty body) with TTL ≈ 7200 seconds. No refresh token — the API key itself is the "refresh".
- **BasketService** (`/BasketService.svc/...`) — OData v2 service for "Kup Teraz" orders. Uses navigation properties to filter by state (e.g. `OrderStates(30)/Orders` returns orders awaiting shop confirmation).

Reference documentation:
- [API Ceneo — biznes.ceneo.pl/api](https://biznes.ceneo.pl/api)
- [Swagger UI — developers.ceneo.pl](https://developers.ceneo.pl/swagger/ui/index)
- [WebApi account / API key](https://shops.ceneo.pl/WebApi/WebApiAccount)

## Configuration

| Field | Description |
|---|---|
| `apiKey` | Ceneo WebApi key obtained at [shops.ceneo.pl/WebApi/WebApiAccount](https://shops.ceneo.pl/WebApi/WebApiAccount) |

The account must also whitelist the source IP of the application (in the Ceneo merchant panel); requests from unlisted addresses receive HTTP 403 with `Odmowa dostępu z adresu ...`.

## Capabilities

| Capability | Status | Endpoint |
|---|---|---|
| Fetch orders awaiting shop confirmation | ✅ | `GET /BasketService.svc/OrderStates(30)/Orders` |
| Confirm order | ✅ | `GET /BasketService.svc/ConfirmOrder?id={guid}` |
| Assign tracking number | ✅ | `GET /BasketService.svc/SetOrderShipment?orderId={guid}&trackingNumber=...&carrierId=...` |
| Mark as sent | ✅ | `GET /BasketService.svc/SendOrder?id={guid}` |
| Publish / unpublish offer | ❌ no-op | Requires Kup Teraz merchant scope — see `CeneoOfferExport` javadoc |
| Invoice upload | ❌ no-op | Not exposed by the Ceneo WebApi (Kup Teraz) |

### Offer export — how to enable later

`exportOffers()` is wired up as a no-op, but the extension path is short and self-contained — roughly one class (`CeneoOfferExport`) and a few lines elsewhere. Once the Ceneo account has a Kup Teraz merchant entitlement, enabling publication is straightforward:

1. Receive the `apiKey` in `CeneoOfferExport` (currently stripped for simplicity).
2. Build an XML payload per offer following Ceneo's "offer feed" schema — `<offers><o id="..." price="..." stock="..." avail="..." name="..."/></offers>`. The exact schema is undocumented publicly; confirm with Ceneo support before going live.
3. Send via `GET /api/v2/function/offer_processor.process_offers/Call` with `apiKey` and URL-encoded `xml` as query parameters (no Bearer auth — this endpoint uses apiKey-in-query).
4. Unpublish by sending the same entry with `stock="0"` and `avail="0"`.

The full recipe also lives in the `CeneoOfferExport` class javadoc.

## Provider Discovery

Registers `CeneoMarketplaceProviderDescriptor` via `ServiceLoader` for automatic discovery by the main application. See the [provider-api README](../provider-api) for registration details.
