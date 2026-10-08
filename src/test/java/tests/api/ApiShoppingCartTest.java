package tests.api;

import data.LoginData;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeEach;
import io.qameta.allure.restassured.AllureRestAssured;
import config.TestConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import helpers.ApiLoginHelper;

//TODO Убрать константу и добавить прекондишен получение bookId
// из новой/существующей книги
public class ApiShoppingCartTest {

    private RequestSpecification requestSpec;
    private String token;
    private int userId;
    private int bookId = 38;


    private void addBookToCart() {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("userId", userId)
                .pathParam("bookId", bookId)
                .when()
                .post("/api/shoppingcart/addToCart/{userId}/{bookId}")
                .then()
                .statusCode(200);
    }

    private void clearCart() {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("userId", userId)
                .when()
                .delete("/api/shoppingcart/{userId}")
                .then()
                .statusCode(200);
    }

    @BeforeEach
    void setUp() {

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(TestConfig.BACKEND_URL)
                .setRelaxedHTTPSValidation()
                .addFilter(new AllureRestAssured())
                .build();

        ApiLoginHelper loginHelper = new ApiLoginHelper(requestSpec);

        Response response = Allure.step("Получаем данные авторизованного пользователя", () -> {
            return loginHelper.login(LoginData.BUYER_USERNAME, LoginData.BUYER_PASSWORD);
        });

        token = response.path("token");
        userId = response.path("userDetails.userId");

    }

    @Test
    @DisplayName("API : Добавляем товар в корзину")
    void addBookToShoppingCart() {
        clearCart();
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("userId", userId)
                .pathParam("bookId", bookId)
                .when()
                .post("/api/shoppingcart/addToCart/{userId}/{bookId}")
                .then()
                .contentType(JSON)
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].book.bookId", equalTo(bookId))
                .body("[0].book.title", equalTo("Самая Лучшая Книга 2"))
                .body("[0].book.author", equalTo("Самый Лучший Автор"))
                .body("[0].book.category", equalTo("Romance"))
                .body("[0].book.price", equalTo(200.00f))
                .body("[0].book.coverFileName", equalTo("Default_image.jpg"))
                .body("[0].quantity", equalTo(1));
    }

    @Test
    @DisplayName("API : Получение списка товаров из корзины")
    void getShoppingCartItems() {
        clearCart();
        addBookToCart();
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("userId", userId)
                .when()
                .get("/api/shoppingcart/{userId}")
                .then()
                .contentType(JSON)
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].book.bookId", equalTo(bookId))
                .body("[0].book.title", equalTo("Самая Лучшая Книга 2"))
                .body("[0].book.author", equalTo("Самый Лучший Автор"))
                .body("[0].book.category", equalTo("Romance"))
                .body("[0].book.price", equalTo(200.00f))
                .body("[0].book.coverFileName", equalTo("Default_image.jpg"))
                .body("[0].quantity", equalTo(1));

    }

    @Test
    @DisplayName("API : Очистка корзины")
    void cleanShoppingCart() {
        addBookToCart();
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("userId", userId)
                .when()
                .delete("/api/shoppingcart/{userId}")
                .then()
                .statusCode(200);
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("userId", userId)
                .when()
                .get("/api/shoppingcart/{userId}")
                .then()
                .statusCode(200)
                .body("", empty());
    }


}
