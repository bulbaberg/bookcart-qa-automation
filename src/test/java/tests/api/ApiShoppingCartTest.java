package tests.api;

import data.CreatedBook;
import data.LoginData;
import helpers.ApiBookHelper;
import helpers.RequestSpecFactory;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;
import org.junit.jupiter.api.BeforeEach;
import io.restassured.specification.RequestSpecification;
import helpers.ApiLoginHelper;


public class ApiShoppingCartTest {

    private RequestSpecification requestSpec;
    private CreatedBook book;
    private String token;
    private int userId;

    private void addBookToCart() {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("userId", userId)
                .pathParam("bookId", book.bookId())
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

        requestSpec = RequestSpecFactory.create();

        ApiLoginHelper loginHelper = new ApiLoginHelper(requestSpec);

        Response response = Allure.step("Получаем данные авторизованного пользователя", () -> {
            return loginHelper.login(LoginData.BUYER_USERNAME, LoginData.BUYER_PASSWORD);
        });

        token = response.path("token");
        userId = response.path("userDetails.userId");

        Response responseAdmin = Allure.step("Получаем JWT администратора", () -> {
            return loginHelper.login(LoginData.ADMIN_USERNAME, LoginData.ADMIN_PASSWORD);
        });

        String tokenAdmin = responseAdmin.path("token");

        ApiBookHelper bookHelper = new ApiBookHelper(
                requestSpec,
                tokenAdmin
        );

        book = bookHelper.createBook();

    }

    @Test
    @DisplayName("API : Добавляем товар в корзину")
    void addBookToShoppingCart() {
        clearCart();
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("userId", userId)
                .pathParam("bookId", book.bookId())
                .when()
                .post("/api/shoppingcart/addToCart/{userId}/{bookId}")
                .then()
                .contentType(JSON)
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].book.bookId", equalTo(book.bookId()))
                .body("[0].book.title", equalTo(book.title()))
                .body("[0].book.author", equalTo(book.author()))
                .body("[0].book.category", equalTo(book.category()))
                .body("[0].book.price", equalTo((float) book.price()))
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
                .body("[0].book.bookId", equalTo(book.bookId()))
                .body("[0].book.title", equalTo(book.title()))
                .body("[0].book.author", equalTo(book.author()))
                .body("[0].book.category", equalTo(book.category()))
                .body("[0].book.price", equalTo((float) book.price()))
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
