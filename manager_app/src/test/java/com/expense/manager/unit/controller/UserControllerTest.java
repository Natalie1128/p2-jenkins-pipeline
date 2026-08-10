package com.expense.manager.unit.controller;

import com.expense.manager.controller.UserController;
import com.expense.manager.dao.UserDao;
import com.expense.manager.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Testing for the UserController")
public class UserControllerTest {

    @Mock
    UserDao dao;

    @InjectMocks
    UserController userController;


    @Test
    @DisplayName("Validate Manager Login - Successful Login")
    void validateManagerLogin_successfulLogin() throws SQLException {

        // Arrange
        User manager = new User(1,
                "Manager123",
                "pass123",
                "Manager"
        );

        Map<String, String> request = new HashMap<>();
        request.put("username", manager.getUsername());
        request.put("password", manager.getPassword());

        when(dao.validateManagerLogin(
                manager.getUsername(),
                manager.getPassword()
        )).thenReturn(manager);


        // Act
        User result = userController.validateManagerLogin(request);


        // Assert
        assertNotNull(result);
        assertEquals("Manager123", result.getUsername());
        assertEquals("Manager", result.getRole());

        verify(dao).validateManagerLogin(
                "Manager123",
                "pass123"
        );
    }


    @Test
    @DisplayName("Validate Manager Login - Unsuccessful Login")
    void validateManagerLogin_unsuccessfulLogin() throws SQLException {

        // Arrange
        Map<String, String> requestNoUsername = new HashMap<>();
        requestNoUsername.put("username", "");
        requestNoUsername.put("password", "password123");


        Map<String, String> requestNoPassword = new HashMap<>();
        requestNoPassword.put("username", "manager1");
        requestNoPassword.put("password", "");


        when(dao.validateManagerLogin(
                requestNoUsername.get("username"),
                requestNoUsername.get("password")
        )).thenReturn(null);


        when(dao.validateManagerLogin(
                requestNoPassword.get("username"),
                requestNoPassword.get("password")
        )).thenReturn(null);


        // Act
        User result1 =
                userController.validateManagerLogin(requestNoUsername);

        User result2 =
                userController.validateManagerLogin(requestNoPassword);


        // Assert
        assertNull(result1);
        assertNull(result2);


        verify(dao).validateManagerLogin(requestNoUsername.get("username"),
                requestNoUsername.get("password")
        );

        verify(dao).validateManagerLogin(requestNoPassword.get("username"),
                requestNoPassword.get("password")
        );
    }


    @Test
    @DisplayName("Validate Manager Login - SQLException")
    void validateManagerLogin_SQLException_Test() throws SQLException {

        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("username", "Manager123");
        request.put("password", "pass123");

        when(dao.validateManagerLogin(
                request.get("username"),
                request.get("password")
        )).thenThrow(new SQLException("Database connection failed"));

        // Act & Assert
        SQLException exception = assertThrows(
                SQLException.class,
                () -> userController.validateManagerLogin(request)
        );

        assertEquals(
                "Database connection failed",
                exception.getMessage()
        );

        verify(dao).validateManagerLogin(
                request.get("username"),
                request.get("password")
        );
    }
    @Test
    @DisplayName("Get All Users")
    void getUsers_returnsUsers() throws SQLException {

        List<User> users = List.of(
                new User(1, "Manager123", "pass123", "Manager"),
                new User(2, "Employee123", "pass123", "Employee")
        );

        when(dao.findAll()).thenReturn(users);

        List<User> result = userController.getUsers();

        assertEquals(2, result.size());
        assertEquals(users, result);

        verify(dao).findAll();
    }

    @Test
    @DisplayName("Find User By ID")
    void findById_returnsUser() throws SQLException {

        User user = new User(
                1,
                "Manager123",
                "pass123",
                "Manager"
        );

        when(dao.findById(1))
                .thenReturn(user);


        User result = userController.findById(1);


        assertNotNull(result);
        assertEquals(user, result);

        verify(dao).findById(1);
    }

    @Test
    @DisplayName("Find User By Username")
    void findByUsername_returnsUser() throws SQLException {

        User user = new User(
                1,
                "Manager123",
                "pass123",
                "Manager"
        );


        when(dao.findByUsername("Manager123"))
                .thenReturn(user);


        User result =
                userController.findByUsername("Manager123");


        assertEquals(user, result);

        verify(dao)
                .findByUsername("Manager123");
    }

}