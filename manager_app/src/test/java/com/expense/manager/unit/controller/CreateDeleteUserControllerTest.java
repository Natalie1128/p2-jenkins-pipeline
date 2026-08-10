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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create/Delete User Controller Tests")
class CreateDeleteUserControllerTest {

    @Mock
    UserDao dao;

    @InjectMocks
    UserController controller;

    @Test
    @DisplayName("Create User - Success")
    void createUser_success() throws SQLException {
        User user = new User(1, "username", "password", "Employee");

        when(dao.create(user)).thenReturn(user);

        User result = controller.createUser(user);

        assertNotNull(result);
        assertEquals("username", result.getUsername());
        assertEquals("Employee", result.getRole());

        verify(dao).create(user);
    }

    @Test
    @DisplayName("Create User - SQLException")
    void createUser_sqlException() throws SQLException {
        User user = new User(1, "username", "password", "Employee");

        when(dao.create(user))
                .thenThrow(new SQLException("Database failed"));

        SQLException exception = assertThrows(
                SQLException.class,
                () -> controller.createUser(user)
        );

        assertEquals("Database failed", exception.getMessage());
        verify(dao).create(user);
    }

    @Test
    @DisplayName("Delete User - Success")
    void deleteUser_success() throws SQLException {
        when(dao.delete(1)).thenReturn(true);

        Map<String, String> result = controller.deleteUser(1);

        assertEquals("User deleted successfully.", result.get("message"));
        verify(dao).delete(1);
    }

    @Test
    @DisplayName("Delete User - User Not Found")
    void deleteUser_notFound() throws SQLException {
        when(dao.delete(999)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> controller.deleteUser(999)
        );

        assertEquals("User not found.", exception.getMessage());
        verify(dao).delete(999);
    }

    @Test
    @DisplayName("Delete User - SQLException")
    void deleteUser_sqlException() throws SQLException {
        when(dao.delete(1))
                .thenThrow(new SQLException("Database failed"));

        SQLException exception = assertThrows(
                SQLException.class,
                () -> controller.deleteUser(1)
        );

        assertEquals("Database failed", exception.getMessage());
        verify(dao).delete(1);
    }
}