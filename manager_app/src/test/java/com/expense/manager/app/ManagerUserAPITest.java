package com.expense.manager.app;

import com.expense.manager.models.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

/*
Note: Because of the unique username constraint, this code assumes that some usernames don't exist.
Run this code with the base database from seed.py.
I chose silly names for the tests, so existing users shouldn't be a problem unless you have a user called
"John Employee" or "John Manager".
 */
@DisplayName("Manager User API")
public class ManagerUserAPITest {

    static List<User> dirtyUsers;

    public static void deleteDirtyUsers() {
        if (dirtyUsers != null && !dirtyUsers.isEmpty()) {
            for (User user: dirtyUsers) {
                given()
                        .when()
                        .delete("/" + user.getId())
                        .then()
                        .statusCode(200);
            }
        }
        dirtyUsers = new ArrayList<>();
    }

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://127.0.0.1:9090/users"; // change this when URI changes!
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        dirtyUsers = new ArrayList<>();
    }

    @AfterEach
    void cleanUp() {
        deleteDirtyUsers();
    }

    @AfterAll
    static void tearDown() {
        deleteDirtyUsers(); // I'm not sure if this does anything.
    }


    @Test
    @DisplayName("GET /users retrieves a list of all users")
    void get_users_RetrieveAllUsers() {
         List<User> resultUsers = given()
                .when()
                .get()
                .then()
                .statusCode(200)
                 .extract().response().jsonPath().getList("$", User.class);
         assertNotNull(resultUsers);
    }

    @Test
    @DisplayName("GET /users/{id} retrieves the specified user")
    void get_usersId_returnSpecificUser() {
        String requestBody = """
                {
                    "username": "John Manager",
                    "password": "admin456",
                    "role": "Manager"
                }
                """;
        // Add the user
        // presumes that the creation path works.
        User expectedUser = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().as(User.class);

        dirtyUsers.add(expectedUser);

        User actualUser = given()
                .when()
                .get("/" + expectedUser.getId())
                .then()
                .statusCode(200)
                .extract().as(User.class);

        assertNotNull(actualUser, "Retrieved null user");

        assertAll(
                () -> assertEquals(expectedUser.getUsername(), actualUser.getUsername(), "Usernames do not match"),
                () -> assertEquals(expectedUser.getPassword(), actualUser.getPassword(), "Passwords do not match"),
                () -> assertEquals(expectedUser.getRole(), actualUser.getRole(), "Roles do not match")
        );
    }

    @Test
    @DisplayName("GET /users/{id} fails when there is no user with a matching ID")
    void get_usersId_nonexistentUserFails() {
        // create a new user
        String requestBody = """
                {
                    "username": "John Manager",
                    "password": "admin456",
                    "role": "Manager"
                }
                """;
        // Add the user
        // presumes that the creation path works.
        User expectedUser = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().as(User.class);


        given()
                .when()
                .delete("/" + expectedUser.getId())
                .then()
                .statusCode(200);

        String responseString = given()
                .when()
                .get("/" + expectedUser.getId())
                .then()
                .statusCode(200)
                .extract().asString();

        assertTrue(responseString.isEmpty(), "Getting a nonexistent user should yield an empty response");
    }

    @Test
    @DisplayName("POST /users creates a new user and retrieves valid response")
    void post_users_createSuccessValidResponse() {
        String requestBody = """
				{
				    "username": "John Manager",
				    "password": "test_password",
				    "role": "Manager"
				}
				""";

        User newUser = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().as(User.class);

        dirtyUsers.add(newUser);

        assertNotNull(newUser, "New user not added");
        assertAll(
                () -> assertEquals("John Manager", newUser.getUsername(), "Created user with incorrect username"),
                () -> assertEquals("test_password", newUser.getPassword(), "Created user with incorrect password"),
                () -> assertEquals("Manager", newUser.getRole(), "Created user with incorrect role")
        );
    }

    @Test
    @DisplayName("POST /users throws appropriate error with invalid input")
    void post_users_invalidInputGetsError() {
        // body has invalid role
        String invalidRequestBody = """
                {
                    "username": "John Roleless",
                    "password": "SuperGoodPassword"
                    "role": "invalid role"
                }
                """;
        given()
                .contentType(ContentType.JSON)
                .body(invalidRequestBody)
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("GET /users/username/{username} retrieves correct user")
    void get_usersUsername_retrieveCorrectUser() {
        String requestBody = """
                {
                    "username": "John Employee",
                    "password": "password",
                    "role": "Employee"
                }
                """;
        User expectedUser = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().as(User.class);
        dirtyUsers.add(expectedUser);
        User actualUser = given()
                .when()
                .get("/username/" + expectedUser.getUsername())
                .then()
                .statusCode(200)
                .extract().as(User.class);

        assertNotNull(actualUser);
        assertAll(
                () -> assertEquals(expectedUser.getId(), actualUser.getId(), "User IDs do not match"),
                () -> assertEquals(expectedUser.getPassword(), actualUser.getPassword(), "Passwords do not match"),
                () -> assertEquals(expectedUser.getRole(), actualUser.getRole(), "Roles do not match")
        );
    }

    @Test
    @DisplayName("GET /users/username/{username} retrieves empty response with no matching username")
    void get_usersUsername_noUserEmptyResponse() {
        String requestBody = """
                {
                    "username": "John Manager",
                    "password": "password",
                    "role": "Manager"
                }
                """;
        User tbdUser = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().as(User.class);

        given()
                .when()
                .delete("/" + tbdUser.getId())
                .then()
                .statusCode(200);

        String responseStr = given()
                .when()
                .get("/username/" + tbdUser.getUsername())
                .then()
                .extract().asString();

        assertTrue(responseStr.isEmpty());
    }

    @Test
    @DisplayName("POST /users/login validates login correctly")
    void post_login_validateUser() {
        String createUserRequestBody = """
                {
                    "username": "John Manager",
                    "password": "password",
                    "role": "Manager"
                }
                """;
        User testUser = given()
                .contentType(ContentType.JSON)
                .body(createUserRequestBody)
                .when()
                .post()
                .then()
                .extract().as(User.class);
        dirtyUsers.add(testUser);

        String loginRequestBody = """
                {
                    "username": "John Manager",
                    "password": "password"
                }
                """;
        User result = given()
                .contentType(ContentType.JSON)
                .body(loginRequestBody)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract().as(User.class);
        assertNotNull(result, "Login result should not be null");
        assertAll(
                () -> assertEquals(testUser.getId(), result.getId()),
                () -> assertEquals(testUser.getUsername(), result.getUsername()),
                () -> assertEquals(testUser.getRole(), result.getRole())
        );
    }

    @Test
    @DisplayName("POST /users/login will not validate employee login")
    void post_login_employeeDontValidate() {
        String createUserRequestBody = """
                {
                    "username": "John Employee",
                    "password": "password",
                    "role": "Employee"
                }
                """;
        User employee = given()
                .contentType(ContentType.JSON)
                .body(createUserRequestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().as(User.class);
        dirtyUsers.add(employee);

        String loginRequestBody = """
                {
                    "username": "John Employee",
                    "password": "password"
                }
                """;
        String response = given()
                .contentType(ContentType.JSON)
                .body(loginRequestBody)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract().asString();
        assertTrue(response.isEmpty(), "Employee login attempt should not yield any validation");
    }

    @Test
    @DisplayName("POST /users/login will not validate login with incorrect credentials")
    void post_login_wrongCredentialsDontValidate() {
        String createUserRequestBody = """
                {
                    "username": "John Manager",
                    "password": "password",
                    "role": "Manager"
                }
                """;
        User employee = given()
                .contentType(ContentType.JSON)
                .body(createUserRequestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().as(User.class);
        dirtyUsers.add(employee);

        String loginRequestBody = """
                {
                    "username": "John Manager",
                    "password": "wrong password"
                }
                """;
        String response = given()
                .contentType(ContentType.JSON)
                .body(loginRequestBody)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract().asString();
        assertTrue(response.isEmpty(), "Login attempt with incorrect credentials should not yield any validation");
    }

    @Test
    @DisplayName("DELETE /users/{id} correctly deletes a user")
    void delete_usersId_deleteUser() {
        String requestBody = """
                {
                    "username": "John Manager",
                    "password": "password",
                    "role": "Manager"
                }
                """;
        User tbdUser = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().as(User.class);

        String message = given()
                .when()
                .delete("/" + tbdUser.getId())
                .then()
                .statusCode(200)
                .extract()
                .path("message");

        assertEquals("User deleted successfully.", message);
    }

    @Test
    @DisplayName("DELETE /users/{id} will get an error if user doesn't exist")
    void delete_usersId_userDoesntExistFails() {
        String requestBody = """
                {
                    "username": "John Manager",
                    "password": "password",
                    "role": "Manager"
                }
                """;
        User tbdUser = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().as(User.class);

        // delete the user...
        String message = given()
                .when()
                .delete("/" + tbdUser.getId())
                .then()
                .statusCode(200)
                .extract()
                .path("message");

        // and then attempt to delete them again.
        given()
                .when()
                .delete("/" + tbdUser.getId())
                .then()
                .statusCode(500);
    }


}

