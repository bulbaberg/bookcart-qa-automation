package tests.api;
import data.LoginData;
import helpers.RequestSpecFactory;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import helpers.ApiLoginHelper;

import org.junit.jupiter.api.BeforeEach;

import io.qameta.allure.restassured.AllureRestAssured;
import config.TestConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import java.util.Locale;

//TODO Добавить проверку созданного заказа
public class ApiCheckOutTest {

    private RequestSpecification requestSpec;
    private String token;
    private int userId;
    private int bookId = 38;
    private String title;
    private String author;
    private String category;
    private float price;
    private String coverFileName;
    private int quantity;
    private float cartTotal;


    @BeforeEach
    void setUp() {

        requestSpec = RequestSpecFactory.create();

        ApiLoginHelper loginHelper = new ApiLoginHelper(requestSpec);

        Response responseUser = Allure.step("Получаем данные авторизованного пользователя", () -> {
            return loginHelper.login(LoginData.BUYER_USERNAME, LoginData.BUYER_PASSWORD);
        });

        token = responseUser.path("token");
        userId = responseUser.path("userDetails.userId");

        Allure.step("Очищаем корзину от товаров", () -> {
            return given()
                    .spec(requestSpec)
                    .header("Authorization", "Bearer " + token)
                    .pathParam("userId", userId)
                    .when()
                    .delete("/api/shoppingcart/{userId}")
                    .then()
                    .statusCode(200);
        });

        Response responseBook = Allure.step("Добавляет товар в корзину", () -> {
                    return given()
                            .spec(requestSpec)
                            .contentType(JSON)
                            .header("Authorization", "Bearer " + token)
                            .pathParam("userId", userId)
                            .pathParam("bookId", bookId)
                            .when()
                            .post("/api/shoppingcart/addToCart/{userId}/{bookId}")
                            .then()
                            .statusCode(200)
                            .extract()
                            .response();
            });



        title = responseBook.path("[0].book.title");
        author = responseBook.path("[0].book.author");
        category = responseBook.path("[0].book.category");
        price = responseBook.path("[0].book.price");
        coverFileName = responseBook.path("[0].book.coverFileName");
        quantity = responseBook.path("[0].quantity");

        cartTotal = quantity * price;

    }

    @AfterEach
    void tearDown() {
        Allure.step("Очищаем корзину от товаров", () -> {
            return given()
                    .spec(requestSpec)
                    .header("Authorization", "Bearer " + token)
                    .pathParam("userId", userId)
                    .when()
                    .delete("/api/shoppingcart/{userId}")
                    .then()
                    .statusCode(200);
        });
    }

    @Test
    @DisplayName("API : Создание заказ")
    void createOrder() {

        String jsonOrderDetails = String.format(
                Locale.US,"""
            {
              "orderDetails": [
                {
                  "book": {
                    "bookId": %d,
                    "title": "%s",
                    "author": "%s",
                    "category": "%s",
                    "price": %.2f,
                    "coverFileName": "%s"
                  },
                  "quantity": %d
                }
              ],
              "cartTotal": %.2f
            }
            """,
                bookId,
                title,
                author,
                category,
                price,
                coverFileName,
                quantity,
                cartTotal
                );

        given()
                .spec(requestSpec)
                .contentType(JSON)
                .header("Authorization", "Bearer " + token)
                .pathParam("userId", userId)
                .body(jsonOrderDetails)
                .when()
                .post("/api/CheckOut/{userId}")
                .then()
                .statusCode(200);
    }
}
