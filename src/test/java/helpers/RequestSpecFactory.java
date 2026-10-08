package helpers;
import config.TestConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RequestSpecFactory {
    public static RequestSpecification create() {
        return new RequestSpecBuilder()
                .setBaseUri(TestConfig.BACKEND_URL)
                .setRelaxedHTTPSValidation()
                .addFilter(new AllureRestAssured())
                .build();
    }
}
