import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class PetStoreApiTest {
    private static final String BASE_URL = "https://petstore.swagger.io/v2";
    private RequestSpecification requestSpec;
    private Map<String, Object> petData;
    Integer petId = 876856123;
    String petName = "Doggie";

    @BeforeClass
    public void setUp() {
        petData = new HashMap<>();
        petData.put("id", petId);
        petData.put("name", petName);
        petData.put("status", PetStatus.AVAILABLE.getValue());

        RestAssured.baseURI = BASE_URL;
        requestSpec = given()
                .contentType(ContentType.JSON);
    }

    @Test
    public void testAddPet() {
        requestSpec
                .body(petData)
                .when()
                .post("/pet")
                .then()
                .statusCode(200);
    }

    @Test(dependsOnMethods = "testAddPet")
    public void testUpdatePetStatus() {
        petData.put("status", PetStatus.SOLD.getValue());

        requestSpec
                .body(petData)
                .when()
                .put("/pet")
                .then()
                .statusCode(200);
    }

    @Test(dependsOnMethods = "testUpdatePetStatus")
    public void testPetIsAdded() {
        requestSpec
                .when()
                .get("/pet/" + petId)
                .then()
                .statusCode(200)
                .body("id", equalTo(petId))
                .body("name", equalTo(petName))
                .body("status", equalTo(PetStatus.SOLD.getValue()));
    }
}
