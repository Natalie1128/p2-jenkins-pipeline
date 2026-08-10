package com.expense.manager.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.expense.manager.api.ManagerApiClient;
import com.expense.manager.models.Expense;
import com.expense.manager.models.User;

public class ReportWriter {
    private final ManagerApiClient apiClient;

    public ReportWriter(ManagerApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public File getAvailableFile(String baseName) {
        File file = new File(baseName + ".csv");

        int count = 1;
        while (file.exists()) {
            file = new File(baseName + " (" + count + ")" + ".csv");
            count++;
        }

        return file;
    }

    public String generateReport(String reportName) throws IOException{
        File file = getAvailableFile(reportName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getName()))) {

            // Header
            writer.write("User ID,Name,Expense Description,Expense Amount");
            writer.newLine();
            List<Expense> expenses = apiClient.findAllExpenses();

            for (Expense expense: expenses){
                String username = "UNKNOWN";
                try{
                    User user = apiClient.findUserById(expense.getUser_id());
                    if (user != null) username = user.getUsername();
                }
                catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    System.out.printf("Unknown username for expense ID: %s\n", expense.getId());
                }
                catch (IOException e){
                    System.out.printf("Unknown username for expense ID: %s\n", expense.getId());
                }
                finally{

                    writer.write(expense.getUser_id()+ "," + username + "," + expense.getDescription()+ "," + expense.getAmount());
                    writer.newLine();
                }
            }
            
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        return "Created: " + file.getName();
    }
    
}
