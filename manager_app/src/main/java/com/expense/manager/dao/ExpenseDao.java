package com.expense.manager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.expense.manager.db.Database;
import com.expense.manager.models.Expense;

@Repository
public class ExpenseDao {

    private final Database database;

    @Autowired
    public ExpenseDao(Database database) {
        this.database = database;
    }

    public Expense findById(int id) throws SQLException {
        String sql = "SELECT * FROM expenses WHERE id = ?";

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapExpense(resultSet);
                }
            }
        }

        return null;
    }

    public List<Expense> findAll() throws SQLException {
        String sql = "SELECT * FROM expenses";
        List<Expense> expenses = new ArrayList<>();

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                expenses.add(mapExpense(resultSet));
            }
        }

        return expenses;
    }

    public List<Expense> findByStatus(String status) throws SQLException {
        String sql = """
                        SELECT e.* 
                        FROM expenses e 
                        JOIN approvals a
                        on e.id = a.expense_id
                        where a.status = ?
                        """;
        List<Expense> expenses = new ArrayList<>();

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, status);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    expenses.add(mapExpense(resultSet));
                }
            }
        }

        return expenses;
    }

    public List<Expense> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM expenses WHERE user_id = ?";
        List<Expense> expenses = new ArrayList<>();

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    expenses.add(mapExpense(resultSet));
                }
            }
        }

        return expenses;
    }

    public List<Expense> findByDate(String date) throws SQLException {
        String sql = "SELECT * FROM expenses WHERE date = ?";
        List<Expense> expenses = new ArrayList<>();

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, date);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    expenses.add(mapExpense(resultSet));
                }
            }
        }

        return expenses;
    }

    private Expense mapExpense(ResultSet resultSet) throws SQLException {
        return new Expense(
            resultSet.getInt("id"),
            resultSet.getInt("user_id"),
            resultSet.getFloat("amount"),
            resultSet.getString("description"),
            resultSet.getString("date")
        );
    }
}
