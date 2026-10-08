package tests.api;
import data.LoginData;
import helpers.RequestSpecFactory;
import io.qameta.allure.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import static io.restassured.http.ContentType.JSON;

import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;
import io.restassured.specification.RequestSpecification;

import static org.hamcrest.Matchers.*;

public class ApiRegisterTest {

    private RequestSpecification requestSpec;

    @BeforeEach
    void setUp() {
        requestSpec = RequestSpecFactory.create();
    }

    @Test
    @Disabled("Заблокирован. Запрос через бэк не создаёт пользователя в системе")
    @DisplayName("Создание нового пользователя")
    @Description("Регистрация и последующая авторизация пользователя.")
    void shouldCreateNewUser() {

        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String firstName = "Matilda";
        String lastName = "TheBestWomen";
        String username = "ApiTestUser" + uniqueId;
        String password = "Qasdf123qwe";
        String gender = "Female";

        String userJson = """
        {
          "firstName": "%s",
          "lastName": "%s",
          "userName": "%s",
          "password": "%s",
          "confirmPassword": "%s",
          "gender": "%s"
        }
        """.formatted(
                firstName,
                lastName,
                username,
                password,
                password,
                gender
        );

        String loginJson = """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username,password);

        Allure.step("Создание нового пользователя", () -> {
            given()
                    .spec(requestSpec)
                    .contentType(JSON)
                    .body(userJson)
            .when()
                    .post("/api/user")
            .then()
                    .statusCode(200);
        });

        Allure.step("Авторизация нового пользователя", () -> {
            given()
                    .spec(requestSpec)
                    .contentType(JSON)
                    .body(loginJson)
            .when()
                    .post("/api/Login")
            .then()
                    .statusCode(200)
                    .body("userDetails.username", equalTo(username))
                    .body("userDetails.userTypeName", equalTo("User"))
                    .body("token", notNullValue());
        });


    }

    @Test
    @DisplayName("API : Проверка доступности username при регистрации")
    void checkValidationUser() {
        Allure.step("Валидация существующего логина", () -> {
            given()
                    .spec(requestSpec)
                    .contentType(JSON)
                    .pathParam("login", LoginData.ADMIN_USERNAME)
            .when()
                    .get("/api/user/validateUserName/{login}")
            .then()
                    .statusCode(200)
                    .body("", equalTo(false));
        });

        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String username = "ApiTestUser" + uniqueId;

        Allure.step("Валидация несуществующего логина", () -> {
            given()
                    .spec(requestSpec)
                    .contentType(JSON)
                    .pathParam("login", username)
            .when()
                    .get("/api/user/validateUserName/{login}")
            .then()
                    .statusCode(200)
                    .body("", equalTo(true));
        });
    }
}
