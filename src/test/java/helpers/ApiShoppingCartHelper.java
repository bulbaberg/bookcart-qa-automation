package helpers;
import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class ApiShoppingCartHelper {

    private final RequestSpecification requestSpec;
    private final String token;
    private final int userId;

    public ApiShoppingCartHelper(RequestSpecification requestSpec, String token, int userId) {
        this.requestSpec = requestSpec;
        this.token = token;
        this.userId = userId;
    }

    @Step("Очистить корзину покупателя через API")
    public void clearCart() {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("userId", userId)
                .when()
                .delete("/api/shoppingcart/{userId}")
                .then()
                .statusCode(200);
    }
}
