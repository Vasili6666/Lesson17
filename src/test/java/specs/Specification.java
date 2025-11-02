package specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import static io.restassured.RestAssured.with;
import static io.restassured.filter.log.LogDetail.BODY;
import static io.restassured.filter.log.LogDetail.STATUS;
import static io.restassured.http.ContentType.JSON;

public class Specification {


    public static RequestSpecification allRequests = with()
            .log().uri()
            .log().body()
            .log().headers()
            .contentType(JSON);


    public static ResponseSpecification successfulLoginResponse200 = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType(JSON) // ← ВАЖНО: указываем Content-Type!
            .log(STATUS)
            .log(BODY)
            .build();


    public static ResponseSpecification successDeleteAllBooksResponse204 = new ResponseSpecBuilder()
            .expectStatusCode(204)
            .log(STATUS)
            .log(BODY)
            .build();


    public static ResponseSpecification successAddBooksResponse201 = new ResponseSpecBuilder()
            .expectStatusCode(201)
            .expectContentType(JSON)
            .log(STATUS)
            .log(BODY)
            .build();


    public static ResponseSpecification successfulUserResponse200 = new ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType(JSON)
            .log(STATUS)
            .log(BODY)
            .build();
}