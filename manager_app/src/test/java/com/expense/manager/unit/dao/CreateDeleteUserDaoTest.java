package com.expense.manager.unit.dao;

import com.expense.manager.dao.UserDao;
import com.expense.manager.db.Database;
import com.expense.manager.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.anyInt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create/Delete User DAO Tests")
class CreateDeleteUserDaoTest {
    @Mock
    Database db;
    @Mock
    Connection connection;
    @Mock
    PreparedStatement ps;
    @Mock
    ResultSet generatedKeys;
    @InjectMocks
    UserDao userDao;

    @Test
    @DisplayName("Create User - Success")
    void createUser_success() throws SQLException {
        User user = new User(1, "username", "password", "Employee");

        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(1);

        User result = userDao.create(user);

        assertNotNull(result);
        assertEquals("username", result.getUsername());
        assertEquals("password", result.getPassword());
        assertEquals("Employee", result.getRole());

        verify(ps).setString(1, "username");
        verify(ps).setString(2, "password");
        verify(ps).setString(3, "Employee");
        verify(ps).executeUpdate();
    }

    @Test
    @DisplayName("Create User - SQLException")
    void createUser_sqlException() throws SQLException {
        when(db.getConnection()).thenThrow(new SQLException("Database failed"));

        User user = new User(1, "username", "password", "Employee");

        SQLException exception = assertThrows(
                SQLException.class,
                () -> userDao.create(user));

        assertEquals("Database failed", exception.getMessage());
    }

    @Test
    @DisplayName("Delete User - Success")
    void deleteUser_success() throws SQLException {
        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        boolean result = userDao.delete(1);

        assertTrue(result);

        verify(ps).setInt(1, 1);
        verify(ps).executeUpdate();
    }

    @Test
    @DisplayName("Delete User - User Not Found")
    void deleteUser_notFound() throws SQLException {
        when(db.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        boolean result = userDao.delete(999);

        assertFalse(result);

        verify(ps).setInt(1, 999);
        verify(ps).executeUpdate();
    }

    @Test
    @DisplayName("Delete User - SQLException")
    void deleteUser_sqlException() throws SQLException {
        when(db.getConnection()).thenThrow(new SQLException("Database failed"));

        SQLException exception = assertThrows(
                SQLException.class,
                () -> userDao.delete(1));

        assertEquals("Database failed", exception.getMessage());
    }
}