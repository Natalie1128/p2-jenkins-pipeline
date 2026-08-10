package com.expense.manager.controller;
import com.expense.manager.models.Expense;
import com.expense.manager.dao.ExpenseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController{
    @Autowired
    private ExpenseDao dao;

    @GetMapping
    public List<Expense> getExpenses() throws SQLException {
        return dao.findAll();
    }

    @GetMapping("/{id}")
    public Expense findById(@PathVariable int id) throws SQLException {
        return dao.findById(id);
    }

    @GetMapping("/status/{status}")
    public List<Expense> findByStatus(@PathVariable String status) throws SQLException {
        return dao.findByStatus(status);
    }

    @GetMapping("/user/{userId}")
    public List<Expense> findByUserId(@PathVariable int userId) throws SQLException {
        return dao.findByUserId(userId);
    }

    @GetMapping("/date/{date}")
    public List<Expense> findByDate(@PathVariable String date) throws SQLException {
        return dao.findByDate(date);
    }

}
