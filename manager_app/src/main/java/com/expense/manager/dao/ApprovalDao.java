package com.expense.manager.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.expense.manager.db.Database;
import com.expense.manager.models.Approval;

@Repository
public class ApprovalDao {

    private final Database database;

    @Autowired
    public ApprovalDao(Database database) {
        this.database = database;
    }

    public Approval findById(int id) throws SQLException {
        String sql = "SELECT * FROM approvals WHERE id = ?";

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapApproval(resultSet);
                }
            }
        }

        return null;
    }

    public Approval findByExpenseId(int expenseId) throws SQLException {
        String sql = "SELECT * FROM approvals WHERE expense_id = ?";

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, expenseId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapApproval(resultSet);
                }
            }
        }

        return null;
    }

    public List<Approval> findAll() throws SQLException {
        String sql = "SELECT * FROM approvals";
        List<Approval> approvals = new ArrayList<>();

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                approvals.add(mapApproval(resultSet));
            }
        }

        return approvals;
    }

    public List<Approval> findPending() throws SQLException {
        String sql = """
            SELECT *
            FROM approvals
            WHERE status = 'pending'           
            """;
        List<Approval> approvals = new ArrayList<>();

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                approvals.add(mapApproval(resultSet));
            }
        }

        return approvals;
    }

    public boolean updateStatus(int expenseId, String status, int reviewerId, String comment) throws SQLException {
        String sql = """
            UPDATE approvals
            SET status = ?, reviewer = ?, comment = ?, review_date = ?
            WHERE expense_id = ?
            """;

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, reviewerId);
            statement.setString(3, comment);
            statement.setString(4, LocalDate.now().toString());
            statement.setInt(5, expenseId);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean addCommentToReviewedExpense(int expenseId, String comment) throws SQLException {
        String sql = """
            UPDATE approvals
            SET comment = ?
            WHERE expense_id = ?
              AND status <> 'pending'
            """;

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, comment);
            statement.setInt(2, expenseId);

            return statement.executeUpdate() > 0;
        }
    }

    private Approval mapApproval(ResultSet resultSet) throws SQLException {
        return new Approval(
            resultSet.getInt("id"),
            resultSet.getInt("expense_id"),
            resultSet.getString("status"),
            resultSet.getInt("reviewer"),
            resultSet.getString("comment"),
            resultSet.getString("review_date")
        );
    }
}
