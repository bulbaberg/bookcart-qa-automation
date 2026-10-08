package helpers;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

public class ApiLoginHelper {

    private final RequestSpecification requestSpec;

    public ApiLoginHelper(RequestSpecification requestSpec) {

        this.requestSpec = requestSpec;
    }

    public Response login(String username, String password) {

        String jsonUser = """
                {
                  "username": "%s",
                  "password": "%s"
                }
                """.formatted(username, password);

        return given()
                .spec(requestSpec)
                .contentType(JSON)
                .body(jsonUser)
                .when()
                .post("/api/Login")
                .then()
                .statusCode(200)
                .extract()
                .response();
    }
}