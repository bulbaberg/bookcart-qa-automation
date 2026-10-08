package config;

public class TestConfig {

    public static final String FRONTEND_URL = "https://localhost:53424";
    public static final String BACKEND_URL = "https://localhost:7073";
    public static final String DB_USER = "bookcart_test";

    public static final String DB_URL =
            "jdbc:sqlserver://localhost;" +
                    "instanceName=SQLEXPRESS;" +
                    "databaseName=BookCartDB;" +
                    "trustServerCertificate=true;";
}
