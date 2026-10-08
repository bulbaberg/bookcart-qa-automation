package tests.api;

import data.LoginData;
import helpers.RequestSpecFactory;
import io.qameta.allure.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;

import io.qameta.allure.restassured.AllureRestAssured;
import config.TestConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class ApiLoginTest {

    private RequestSpecification requestSpec;

    @BeforeEach
    void setUp() {
        requestSpec = RequestSpecFactory.create();
    }

    @Test
    @DisplayName("API : Авторизация пользователя")
    void shouldLoginAsAdminViaApi() {

        String jsonUser = """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(LoginData.ADMIN_USERNAME, LoginData.ADMIN_PASSWORD);


        Allure.step("Проверка успешной авторизации", () -> {
                given()
                .spec(requestSpec)
                .contentType(JSON)
                .body(jsonUser)
        .when()
                .post("/api/Login")
        .then()
                .statusCode(200)
                .body("token", not(emptyString()));
        });
    }

    @Test
    @DisplayName("API : Ошибка авторизации при неправильном пароле")
    void shouldRejectLoginWithInvalidPassword() {

        String jsonUser = """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(LoginData.ADMIN_USERNAME, LoginData.INVALID_PASSWORD);


        Allure.step("Ошибка логина/пароля при авторизации", () -> {
            given()
                    .spec(requestSpec)
                    .contentType(JSON)
                    .body(jsonUser)
            .when()
                    .post("/api/Login")
            .then()
                    .statusCode(401);
        });
    }

}