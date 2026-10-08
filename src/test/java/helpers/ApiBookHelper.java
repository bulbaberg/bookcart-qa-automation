package helpers;
import data.CreatedBook;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.Step;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class ApiBookHelper {

    private final RequestSpecification requestSpec;
    private final String token;

    public ApiBookHelper(RequestSpecification requestSpec, String token) {
        this.requestSpec = requestSpec;
        this.token = token;
    }

    @Step("Создать уникальную тестовую книгу через API")
    public CreatedBook createBook() {

        String uniqueId = UUID.randomUUID().toString();

        String title = "Automation Test Book " + uniqueId;
        String author = "Test Author";
        String category = "Fantasy";
        int price = 999;

        String bookJson = """
        {
          "bookId": 0,
          "title": "%s",
          "author": "%s",
          "category": "%s",
          "price": %d
        }
        """.formatted(title, author, category, price);

        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .multiPart("bookFormData", bookJson)
                .when()
                .post("/api/Book")
                .then()
                .statusCode(200);

        int bookId = given()
                .spec(requestSpec)
                .when()
                .get("/api/Book")
                .then()
                .statusCode(200)
                .extract()
                .path("find { it.title == '%s' }.bookId".formatted(title));

        return new CreatedBook(
                bookId,
                title,
                author,
                category,
                price
        );
    }
}