package tests;

import models.ErrorsModel;
import models.RequestBodyModel;
import models.ResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static specs.LoginSpec.requestSpec;
import static specs.LoginSpec.responseSpec;

public class AuthApiTest {
    RequestBodyModel requestBody = new RequestBodyModel();

    @Test
    @DisplayName("Проверка успешного создания пользователя")
    void testPostCreate() {
        requestBody.setName("Testovui");
        requestBody.setJob("QA");
        ResponseModel response = step("Отправка post запроса на создание пользователя", () ->
                given(requestSpec)
                        .body(requestBody)
                        .when()
                        .post("/users")
                        .then()
                        .spec(responseSpec)
                        .statusCode(201)
                        .extract().as(ResponseModel.class));
        step("Проверка введенного имени", () ->
                assertThat(response.getName()).isEqualTo(requestBody.getName()));
        step("Проверка введенной работы", () ->
                assertThat(response.getJob()).isEqualTo(requestBody.getJob()));
    }
    @Test
    @DisplayName("Проверка успешного удаления")
    void testDelete() {
        step("delete запрос на удаление", () ->
                given(requestSpec)
                        .when()
                        .delete("/users/2")
                        .then()
                        .spec(responseSpec)
                        .statusCode(204));

    }
    @Test
    @DisplayName("Проверка неудачной регистрации")
    void testRegisterUnsuccessful() {

        requestBody.setEmail("incorrectemail@");

        String expectedError = "Missing password";

        ErrorsModel responsError = step("Отправка post запроса c некорректым email", () ->
                given(requestSpec)
                        .body(requestBody)
                        .when()
                        .post("/register")
                        .then()
                        .spec(responseSpec)
                        .statusCode(400)
                        .extract().as(ErrorsModel.class));

        step("Проверка получения ошибки", () ->
                assertThat(responsError.getError()).isEqualTo(expectedError));
    }
}
