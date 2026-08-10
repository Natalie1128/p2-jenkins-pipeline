package com.expense.manager.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.expense.manager.models.Approval;
import com.expense.manager.models.Expense;
import com.expense.manager.models.User;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ManagerApiClient {
    //private static final String DEFAULT_BASE_URL = "http://100.84.247.62:9090";
    private static final String DEFAULT_BASE_URL = "http://localhost:9090";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String baseUrl;
    private final HttpClient httpClient;

    public ManagerApiClient() {
        this(resolveBaseUrl());
    }

    public ManagerApiClient(String baseUrl) {
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.httpClient = HttpClient.newHttpClient();
    }

    public User validateManagerLogin(String username, String password) throws IOException, InterruptedException {
        Map<String, String> payload = Map.of(
            "username", username,
            "password", password
        );

        return post("/users/login", payload, User.class);
    }

    public User findUserById(int id) throws IOException, InterruptedException {
        return get("/users/" + id, User.class);
    }

    public Expense findExpenseById(int id) throws IOException, InterruptedException {
        return get("/expenses/" + id, Expense.class);
    }

    public List<Expense> findAllExpenses() throws IOException, InterruptedException {
        return get("/expenses", new TypeReference<List<Expense>>() {});
    }

    public List<Expense> findExpensesByStatus(String status) throws IOException, InterruptedException {
        return get("/expenses/status/" + encode(status), new TypeReference<List<Expense>>() {});
    }

    public List<Expense> findExpensesByUserId(int userId) throws IOException, InterruptedException {
        return get("/expenses/user/" + userId, new TypeReference<List<Expense>>() {});
    }

    public List<Expense> findExpensesByDate(String date) throws IOException, InterruptedException {
        return get("/expenses/date/" + encode(date), new TypeReference<List<Expense>>() {});
    }

    public Approval findApprovalByExpenseId(int expenseId) throws IOException, InterruptedException {
        for (Approval approval : findAllApprovals()) {
            if (approval.getExpense_id() == expenseId) {
                return approval;
            }
        }
        return null;
    }

    public void updateApprovalStatus(int expenseId, String status, int reviewerId, String comment)
            throws IOException, InterruptedException {
        Approval approval = new Approval();
        approval.setStatus(status);
        approval.setReviewer(reviewerId);
        approval.setComment(comment);

        put("/approvals/" + expenseId, approval);
    }

    private List<Approval> findAllApprovals() throws IOException, InterruptedException {
        return get("/approvals", new TypeReference<List<Approval>>() {});
    }

    private <T> T get(String path, Class<T> responseType) throws IOException, InterruptedException {
        HttpRequest request = request(path)
            .GET()
            .build();

        return send(request, responseType);
    }

    private <T> T get(String path, TypeReference<T> responseType) throws IOException, InterruptedException {
        HttpRequest request = request(path)
            .GET()
            .build();

        return send(request, responseType);
    }

    private <T> T post(String path, Object payload, Class<T> responseType) throws IOException, InterruptedException {
        HttpRequest request = request(path)
            .POST(jsonBody(payload))
            .header("Content-Type", "application/json")
            .build();

        return send(request, responseType);
    }

    private void put(String path, Object payload) throws IOException, InterruptedException {
        HttpRequest request = request(path)
            .PUT(jsonBody(payload))
            .header("Content-Type", "application/json")
            .build();

        send(request, Void.class);
    }

    private HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder(URI.create(baseUrl + path))
            .header("Accept", "application/json");
    }

    private HttpRequest.BodyPublisher jsonBody(Object payload) throws IOException {
        return HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(payload));
    }

    private <T> T send(HttpRequest request, Class<T> responseType) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 404) {
            return null;
        }
        ensureSuccess(response);
        if (responseType == Void.class || response.body().isBlank()) {
            return null;
        }
        return MAPPER.readValue(response.body(), responseType);
    }

    private <T> T send(HttpRequest request, TypeReference<T> responseType) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ensureSuccess(response);
        return MAPPER.readValue(response.body(), responseType);
    }

    private void ensureSuccess(HttpResponse<String> response) throws IOException {
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("HTTP " + response.statusCode() + ": " + response.body());
        }
    }

    private static String resolveBaseUrl() {
        String propertyUrl = System.getProperty("manager.api.url");
        if (propertyUrl != null && !propertyUrl.isBlank()) {
            return propertyUrl;
        }

        String environmentUrl = System.getenv("MANAGER_API_URL");
        if (environmentUrl != null && !environmentUrl.isBlank()) {
            return environmentUrl;
        }

        return DEFAULT_BASE_URL;
    }

    private static String trimTrailingSlash(String value) {
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
