package com.expense.manager.controller;
import com.expense.manager.models.Approval;
import com.expense.manager.dao.ApprovalDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/approvals")
public class ApprovalController{
    @Autowired
    private ApprovalDao dao;

    @GetMapping
    public List<Approval> getApprovals() throws SQLException {
        return dao.findAll();
    }

    @GetMapping("/{id}")
    public Approval findById(@PathVariable int id) throws SQLException {
        return dao.findById(id);
    }

    @PutMapping("/{id}")
    public void updateApproval(
            @PathVariable int id,
            @RequestBody Approval approval) throws SQLException {

        dao.updateStatus(id, approval.getStatus(), approval.getReviewer(), approval.getComment());
    }

}
