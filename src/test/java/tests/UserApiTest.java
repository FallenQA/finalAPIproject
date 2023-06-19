package tests;


import models.RequestBodyModel;
import models.ResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static specs.LoginSpec.requestSpec;
import static specs.LoginSpec.responseSpec;


@DisplayName("HW Rest Api, models and specs")
public class UserApiTest {
    RequestBodyModel requestBody = new RequestBodyModel();
    @Test
    @DisplayName("Проверка успешного редактирования пользователя")
    void testPutUpdate() {
        requestBody.setName("Testovui");
        requestBody.setJob("QA");
        ResponseModel response = step("put запрос на изменение данных", () ->
                given(requestSpec)
                        .body(requestBody)
                        .when()
                        .put("/users/2")
                        .then()
                        .spec(responseSpec)
                        .statusCode(200)
                        .extract().as(ResponseModel.class));
        step("Проверка введенного имени", () ->
                assertThat(response.getName()).isEqualTo(requestBody.getName()));
        step("Проверка введенной работы", () ->
                assertThat(response.getJob()).isEqualTo(requestBody.getJob()));
    }
    @Test
    @DisplayName("Проверка, что пользователь не найден")
    void testSingleUserNotFound() {
        step("Отправка get запроса по несуществующему пользователю", () ->
                given(requestSpec)
                        .when()
                        .get("/users/23")
                        .then()
                        .spec(responseSpec)
                        .statusCode(404));
    }
    @Test
    @DisplayName("Проверка JsonScheme")
        //https://www.liquid-technologies.com/online-json-to-schema-converter
    void testSingleResource() {
        step("отправить GET запрос на схему JSON", () ->
                given(requestSpec)
                        .when()
                        .get("/unknown/2")
                        .then()
                        .spec(responseSpec)
                        .statusCode(200)
                        .body(matchesJsonSchemaInClasspath("scheme/jsonscheme_resp.json")));
    }
    @Test
    @DisplayName("Проверка получения листа данных")
    void testSingleUserLombok() {
        step("Запрашием получение листа данных", () ->
                given(requestSpec)
                        .when()
                        .get("/users?page=2")
                        .then()
                        .statusCode(200)
                        .spec(responseSpec)
                        .body("data.findAll { it.first_name == 'Michael' }",
                                hasItems(hasEntry("first_name", "Michael"), hasEntry("last_name", "Lawson"))));
    }
}
