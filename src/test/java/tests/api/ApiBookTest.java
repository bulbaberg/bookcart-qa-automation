package tests.api;
import data.LoginData;
import helpers.RequestSpecFactory;
import io.qameta.allure.*;
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
import java.util.List;
import java.util.UUID;


@Epic("BookCart")
@Feature("Books API")
public class ApiBookTest {

    private RequestSpecification requestSpec;
    private String token;

    @BeforeEach
    void setUp() {

        requestSpec = RequestSpecFactory.create();

        ApiLoginHelper loginHelper = new ApiLoginHelper(requestSpec);

        Response response = Allure.step("Получаем JWT", () -> {
            return loginHelper.login(LoginData.ADMIN_USERNAME, LoginData.ADMIN_PASSWORD);
        });

        token = response.path("token");

    }

    @Test
    @DisplayName("API : создание, изменение и удаление книги")
    @Description("Создание, изменение и удаление книги через API с последующей верефикацией результата")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Book CRUD")
    void shouldCreateUpdateAndDeleteBook() {

        String uniqueId = UUID.randomUUID().toString();

        String title = "API Test Book " + uniqueId;
        String author = "API Test Author";
        String category = "Fantasy";
        int price = 999;
        int updatePrice = 1100;

        String bookJson = """
            {
              "bookId": 0,
              "title": "%s",
              "author": "%s",
              "category": "%s",
              "price": %d
            }
            """.formatted(title, author, category, price);


        //создание книги
        Allure.step("Создаём книгу " + title, () -> {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .multiPart("bookFormData", bookJson)
        .when()
                .post("/api/Book")
        .then()
                .statusCode(200);
        });

        //получение id созданной книги
        int bookId = Allure.step("Получаем ID созданной книги", () -> {
            return given()
                .spec(requestSpec)
            .when()
                .get("/api/Book")
            .then()
                .statusCode(200)
                .body(
                        "find { it.title == '%s' }.author".formatted(title),
                        equalTo(author)
                )
                .body(
                        "find { it.title == '%s' }.category".formatted(title),
                        equalTo(category)
                )
                .body(
                        "find { it.title == '%s' }.price".formatted(title),
                        equalTo((float) price)
                )
            .extract()
                .path("find { it.title == '%s' }.bookId".formatted(title));
        });

        String updateBookJson = """
                {
                    "bookId":%d,
                    "title":"%s",
                    "author":"%s",
                    "category":"%s",
                    "price":%d
                }
                """.formatted(bookId, title, author, category, updatePrice);

        //изменить книгу по полученному id
        Allure.step("Изменить книгу по полученному id", () -> {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .multiPart("bookFormData", updateBookJson)
        .when()
                .put("/api/Book")
        .then()
                .statusCode(200);

        //проверка изменение цены книги
        given()
                .spec(requestSpec)
        .when()
                .get("/api/Book")
        .then()
                .statusCode(200)
                .body("find { it.title == '%s' }.price".formatted(title),
                        equalTo((float) updatePrice)
                );
        });

        //удаляем книгу по переданному id
        Allure.step("Удаляем книгу по переданному id", () -> {
        given()
                .spec(requestSpec)
                .header("Authorization", "Bearer " + token)
                .pathParam("id", bookId)
        .when()
                .delete("/api/Book/{id}")
        .then()
                .statusCode(200);

        //проверка удаление книги
        given()
                .spec(requestSpec)
        .when()
                .get("/api/Book")
        .then()
                .statusCode(200)
                .body("find { it.title == '%s' }".formatted(title),
                        nullValue()
                );
        });

    }


    @Test
    @DisplayName("API : Получение созданной книги по названию")
    void shouldGetBooks() {
        given()
                .spec(requestSpec)
        .when()
                .get("/api/Book")
        .then()
                .statusCode(200)
                .contentType(JSON)
                .body("size()", greaterThan(0))
                .body("find { it.title == 'Убить пересмешника' }.bookId", greaterThan(0))
                .body("find { it.title == 'Убить пересмешника' }.title", equalTo("Убить пересмешника"))
                .body("find { it.title == 'Убить пересмешника' }.author", equalTo("Харпер Ли"))
                .body("find { it.title == 'Убить пересмешника' }.category", equalTo("Fantasy"))
                .body("find { it.title == 'Убить пересмешника' }.price", equalTo(999.0f))
                .body("find { it.title == 'Убить пересмешника' }.coverFileName", equalTo("Default_image.jpg"));


    }

    @Test
    @DisplayName("API : Проверка получения 401 кода ошибки при создании книги без авторизации")
    void shouldRejectBookCreationWithoutAuthorization() {

        String bookJson = """
            {
              "bookId": 0,
              "title": "Unauthorized API Test Book",
              "author": "API Test Author",
              "category": "Fantasy",
              "price": 999
            }
            """;

        given()
                .spec(requestSpec)
                .multiPart("bookFormData", bookJson)
        .when()
                .post("/api/Book")
        .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("API : Проверка получения массива книг")
    void shouldGetBooksList() {
        given()
                .spec(requestSpec)
        .when()
                .get("/api/Book")
        .then()
                .contentType(JSON)
                .statusCode(200)
                .body("", instanceOf(List.class))
                .body("bookId", everyItem(greaterThan(0)))
                .body("title", everyItem(not(emptyOrNullString())))
                .body("size()", greaterThan(0));
    }
}

