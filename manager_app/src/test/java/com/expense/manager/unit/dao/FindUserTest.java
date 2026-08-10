package com.expense.manager.unit.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.expense.manager.dao.UserDao;
import com.expense.manager.db.Database;
import com.expense.manager.models.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("Find User unit Tests")
public class FindUserTest {
        @Mock
        Database database;

        @Mock
        Connection connection;

        @Mock
        PreparedStatement statement;

        @Mock
        ResultSet resultSet;

        UserDao userDao;

        @BeforeEach
        void setup() {
                userDao = new UserDao(database);
        }

        @Test
        void findById_validId_returnsUser() throws SQLException {
                when(database.getConnection()).thenReturn(connection);
                when(connection.prepareStatement(anyString()))
                                .thenReturn(statement);

                when(statement.executeQuery())
                                .thenReturn(resultSet);

                when(resultSet.next())
                                .thenReturn(true);

                when(resultSet.getInt("id"))
                                .thenReturn(1);
                when(resultSet.getString("username"))
                                .thenReturn("Oscar");
                when(resultSet.getString("password"))
                                .thenReturn("password");
                when(resultSet.getString("role"))
                                .thenReturn("Employee");

                User result = userDao.findById(1);

                assertEquals(1, result.getId());
                assertEquals("Oscar", result.getUsername());
        }

        @Test
        void findById_invalidId_returnsNull() throws SQLException {
                when(database.getConnection()).thenReturn(connection);
                when(connection.prepareStatement(anyString()))
                                .thenReturn(statement);

                when(statement.executeQuery())
                                .thenReturn(resultSet);

                when(resultSet.next())
                                .thenReturn(false);

                User result = userDao.findById(999);

                assertNull(result);
        }

        @Test
        void findByUsername_existingUsername_returnsUser() throws SQLException {
                when(database.getConnection()).thenReturn(connection);
                when(connection.prepareStatement(anyString()))
                                .thenReturn(statement);

                when(statement.executeQuery())
                                .thenReturn(resultSet);

                when(resultSet.next())
                                .thenReturn(true);

                when(resultSet.getInt("id"))
                                .thenReturn(1);
                when(resultSet.getString("username"))
                                .thenReturn("Oscar");

                User result = userDao.findByUsername("Oscar");

                assertEquals("Oscar", result.getUsername());
        }

        @Test
        void findAll_returnsAllUsers() throws SQLException {

                when(database.getConnection()).thenReturn(connection);
                when(connection.prepareStatement(anyString()))
                                .thenReturn(statement);

                when(statement.executeQuery())
                                .thenReturn(resultSet);

                when(resultSet.next())
                                .thenReturn(true)
                                .thenReturn(true)
                                .thenReturn(false);

                User user1 = new User();
                User user2 = new User();

                when(resultSet.getInt("id"))
                                .thenReturn(1)
                                .thenReturn(2);

                List<User> result = userDao.findAll();

                assertEquals(2, result.size());
        }

}
