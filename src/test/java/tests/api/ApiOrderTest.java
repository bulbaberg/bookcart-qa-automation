package tests.api;
import data.LoginData;
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

import java.util.Locale;
import java.util.UUID;

public class ApiOrderTest {

    private RequestSpecification requestSpec;
    private String tokenUser;
    private String tokenAdmin;
    private int userId;
    private int bookId;
    private String author = "Api Order Test Author";
    private String category = "Fantasy";
    private float price = 669.0f;
    private String coverFileName;
    private int quantity;
    private float cartTotal;
    private String title = "Api Order Test Book " + UUID.randomUUID();


    @BeforeEach
    void setUp() {

        requestSpec = RequestSpecFactory.create();

        ApiLoginHelper loginHelper = new ApiLoginHelper(requestSpec);

        Response responseUser = Allure.step("Получаем данные авторизованного покупателя", () -> {
            return loginHelper.login(LoginData.BUYER_USERNAME, LoginData.BUYER_PASSWORD);
        });

        tokenUser = responseUser.path("token");
        userId = responseUser.path("userDetails.userId");

        Response responseAdmin = Allure.step("Получаем JWT администратора", () -> {
            return loginHelper.login(LoginData.ADMIN_USERNAME, LoginData.ADMIN_PASSWORD);
        });

        tokenAdmin = responseAdmin.path("token");

        Allure.step("Создаём книгу " + title, () -> {

            String bookJson = String.format(
                    Locale.US,"""
            {
              "bookId": 0,
              "title": "%s",
              "author": "%s",
              "category": "%s",
              "price": %.2f
            }
            """,title, author, category, price);

            given()
                    .spec(requestSpec)
                    .header("Authorization", "Bearer " + tokenAdmin)
                    .multiPart("bookFormData", bookJson)
                    .when()
                    .post("/api/Book")
                    .then()
                    .statusCode(200);
        });


        Response responseId = Allure.step("Получаем id созданной книги", () -> {
            return given()
                    .spec(requestSpec)
                    .when()
                    .get("/api/Book")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();

        });

        bookId = responseId.path("find { it.title == '" + title + "'}.bookId");

        Allure.step("Очистили корзину от лишних товаров", () -> {
            given()
                    .spec(requestSpec)
                    .header("Authorization", "Bearer " + tokenUser)
                    .pathParam("userId", userId)
                    .when()
                    .delete("/api/shoppingcart/{userId}")
                    .then()
                    .statusCode(200);
        });

        Response responseBook = Allure.step("Добавили товар в корзину", () -> {
            return given()
                    .spec(requestSpec)
                    .header("Authorization", "Bearer " + tokenUser)
                    .pathParam("userId", userId)
                    .pathParam("bookId", bookId)
                    .when()
                    .post("/api/shoppingcart/addToCart/{userId}/{bookId}")
                    .then()
                    .statusCode(200)
                    .extract()
                    .response();
        });


        coverFileName = responseBook.path("[0].book.coverFileName");
        quantity = responseBook.path("[0].quantity");
        cartTotal = quantity * price;

        Allure.step("Создание заказа", () -> {

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
                    .header("Authorization", "Bearer " + tokenUser)
                    .pathParam("userId", userId)
                    .body(jsonOrderDetails)
                    .when()
                    .post("/api/CheckOut/{userId}")
                    .then()
                    .statusCode(200);
        });

    }

    @Test
    @DisplayName("API : Проверка созданного заказа пользователя")
    void checkCreatedUserOrder() {
        Allure.step("Проверка созданного заказа", () -> {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + tokenUser)
                .pathParam("userId", userId)
                .when()
                .get("/api/Order/{userId}")
                .then()
                .contentType(JSON)
                .statusCode(200)
                .body("find { it.orderDetails[0].book.title == '" + title + "'}.orderDetails[0].book.title", equalTo(title))
                .body("find { it.orderDetails[0].book.title == '" + title + "'}.orderDetails[0].book.bookId", equalTo(bookId))
                .body("find { it.orderDetails[0].book.title == '" + title + "'}.orderDetails[0].book.price", equalTo(price))
                .body("find { it.orderDetails[0].book.title == '" + title + "'}.orderDetails[0].quantity", equalTo(quantity))
                .body("find { it.orderDetails[0].book.title == '" + title + "'}.cartTotal", equalTo(cartTotal))
                .body("find { it.orderDetails[0].book.title == '" + title + "'}.orderId", notNullValue());

        });

    }
}
