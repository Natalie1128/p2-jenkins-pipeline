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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDao unit Tests")
public class UserDaoTest {

        @Mock
        Database db;
        @Mock
        Connection connection;
        @Mock
        PreparedStatement ps;
        @Mock
        ResultSet rs;

        @InjectMocks
        UserDao userDao;

        @Test
        @DisplayName("validate Manager Login- Valid Credentials Returns Usser")
        void validateManagerLogin_validCredentials_returnsUser() throws SQLException {

                // arrange
                when(db.getConnection())
                                .thenReturn(connection);

                when(connection.prepareStatement(anyString()))
                                .thenReturn(ps);

                when(ps.executeQuery())
                                .thenReturn(rs);

                when(rs.next())
                                .thenReturn(true);

                when(rs.getInt("id"))
                                .thenReturn(1);

                when(rs.getString("username"))
                                .thenReturn("Manager123");

                when(rs.getString("password"))
                                .thenReturn("pass123");

                when(rs.getString("role"))
                                .thenReturn("Manager");

                // act
                User result = userDao.validateManagerLogin(
                                "Manager123",
                                "pass123");

                // assert
                assertNotNull(result);

                assertEquals(1, result.getId());
                assertEquals("Manager123", result.getUsername());
                assertEquals("pass123", result.getPassword());
                assertEquals("Manager", result.getRole());

                verify(ps).setString(1, "Manager123");
                verify(ps).setString(2, "pass123");

        }

        @Test
        @DisplayName("Validate Manager Login - Invalid Credentials Returns Null")
        void validateManagerLogin_invalidCredentials_returnsNull() throws SQLException {

                // Arrange
                when(db.getConnection())
                                .thenReturn(connection);

                when(connection.prepareStatement(anyString()))
                                .thenReturn(ps);

                when(ps.executeQuery())
                                .thenReturn(rs);

                when(rs.next())
                                .thenReturn(false);

                // Act
                User result = userDao.validateManagerLogin(
                                "WrongUser",
                                "WrongPassword");

                // Assert
                assertNull(result);

        }

        @Test
        @DisplayName("Validate Manager Login - Database Connection Throws SQLException")
        void validateManagerLogin_connectionFailure_throwsSQLException() throws SQLException {

                // Arrange
                when(db.getConnection())
                                .thenThrow(new SQLException("Database connection failed"));

                // Act
                SQLException exception = assertThrows(
                                SQLException.class,
                                () -> userDao.validateManagerLogin(
                                                "Manager123",
                                                "pass123"));

                // Assert
                assertEquals(
                                "Database connection failed",
                                exception.getMessage());
        }

        @Test
        @DisplayName("Validate Manager Login - Query Execution Throws SQLException")
        void validateManagerLogin_queryFailure_throwsSQLException() throws SQLException {

                // Arrange
                when(db.getConnection())
                                .thenReturn(connection);

                when(connection.prepareStatement(anyString()))
                                .thenReturn(ps);

                when(ps.executeQuery())
                                .thenThrow(new SQLException("Query failed"));

                // Act
                SQLException exception = assertThrows(
                                SQLException.class,
                                () -> userDao.validateManagerLogin(
                                                "Manager123",
                                                "pass123"));

                // assert
                assertEquals(
                                "Query failed",
                                exception.getMessage());
        }

}
