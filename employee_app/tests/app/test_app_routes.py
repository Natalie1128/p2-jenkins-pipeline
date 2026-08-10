from unittest.mock import patch, MagicMock

from app import app
from app import handle_get_backend_health, handle_get_all_approvals, handle_create_approval, handle_get_approval_by_id, handle_get_approval_by_expense_id, handle_create_user, handle_delete_user, handle_get_all_users, handle_get_user_by_id, handle_login, handle_create_expense,handle_edit_expense, handle_delete_expense, handle_delete_approval, handle_get_all_expenses, handle_get_expense_by_id, handle_get_expenses_by_user, handle_get_non_pending_user_expenses
from models.users import User
from models.approvals import Approval
from models.expenses import Expense


class TestHealthEndpoint:
    def setup_method(self):
        self.client = app.test_client()

    def test_health(self):
        response = self.client.get("/health")

        assert response.status_code == 200
        assert response.get_json() == {"status": "ok"}


class TestApprovalEndpoints:
    def setup_method(self):
        self.client = app.test_client()

    @patch("app.approvals.get_all")
    def test_get_all_approvals(self, mock_get):
        mock_get.return_value = [
            Approval(40, "approved", 7, "Looks good", "2026-07-23", 1),
            Approval(41, "pending", None, None, None, 2)
        ]

        response = self.client.get("/approvals")

        assert response.status_code == 200
        data = response.get_json()

        assert len(data) == 2
        assert data[0]["id"] == 1
        assert data[1]["expense_id"] == 41

    @patch("app.approvals.create")
    def test_create_approval(self, mock_create):
        mock_create.return_value = Approval(
            40,
            "approved",
            7,
            "Looks good",
            "2026-07-23",
            1
        )

        response = self.client.post(
            "/approvals",
            json={
                "expense_id": 40,
                "status": "approved",
                "reviewer": 7,
                "comment": "Looks good",
                "review_date": "2026-07-23"
            }
        )

        assert response.status_code == 201
        assert response.get_json()["id"] == 1

    @patch("app.approvals.get_from_id")
    def test_get_approval_success(self, mock_get):
        mock_get.return_value = Approval(
            40,
            "approved",
            7,
            "Looks good",
            "2026-07-23",
            1
        )

        response = self.client.get("/approvals/1")

        assert response.status_code == 200
        assert response.get_json()["id"] == 1

    @patch("app.approvals.get_from_id")
    def test_get_approval_not_found(self, mock_get):
        mock_get.return_value = None

        response = self.client.get("/approvals/999")

        assert response.status_code == 404

    @patch("app.approvals.get_from_expenseid")
    def test_get_approval_by_expense_success(self, mock_get):
        mock_get.return_value = Approval(
            40,
            "approved",
            7,
            "Looks good",
            "2026-07-23",
            1
        )

        response = self.client.get("/approvals/expense/40")

        assert response.status_code == 200

    @patch("app.approvals.get_from_expenseid")
    def test_get_approval_by_expense_not_found(self, mock_get):
        mock_get.return_value = None

        response = self.client.get("/approvals/expense/999")

        assert response.status_code == 404

    @patch("app.approvals.remove")
    @patch("app.approvals.get_from_id")
    def test_delete_approval_success(self, mock_get, mock_remove):
        mock_get.return_value = Approval(40)

        response = self.client.delete("/approvals/1")

        assert response.status_code == 200
        mock_remove.assert_called_once_with(1)

    @patch("app.approvals.get_from_id")
    def test_delete_approval_not_found(self, mock_get):
        mock_get.return_value = None

        response = self.client.delete("/approvals/999")

        assert response.status_code == 404

class TestUserEndpoints:
    def setup_method(self):
            self.client = app.test_client()

    @patch("app.users.create")
    def test_create_user(self, mock_create):
        mock_create.return_value = User(
            "Oscar",
            "Password123",
            "Employee"
        )
        response = self.client.post(
            "/users", 
            json={
                "username": "Oscar",
                "password": "Password123",
                "role": "Employee"
            }
        )
        assert response.status_code == 201
        assert response.get_json()["username"] == "Oscar"
        assert response.get_json()["password"] == "Password123"
        assert response.get_json()["role"] == "Employee"

    @patch("app.users.get_all")
    def test_get_all_users(self, mock_get_all):
        mock_get_all.return_value = [
            User("Oscar", "Password123", "Employee", 1),
            User("Alice", "Password456", "Manager", 2)
        ]

        response = self.client.get("/users")

        assert response.status_code == 200

        data = response.get_json()

        assert len(data) == 2
        assert data[0]["id"] == 1
        assert data[0]["username"] == "Oscar"
        assert data[1]["id"] == 2
        assert data[1]["role"] == "Manager"

    @patch("app.users.get_from_id")
    def test_get_user_by_id_success(self, mock_get):
        mock_get.return_value = User(
            "Oscar",
            "Password123",
            "Employee",
            1
        )

        response = self.client.get("/users/1")

        assert response.status_code == 200

        data = response.get_json()

        assert data["id"] == 1
        assert data["username"] == "Oscar"
        assert data["role"] == "Employee"

    @patch("app.users.get_from_id")
    def test_get_user_by_id_not_found(self, mock_get):
        mock_get.return_value = None

        response = self.client.get("/users/999")

        assert response.status_code == 404

    @patch("app.users.get_from_username_password")
    def test_login_success(self, mock_login):
        mock_login.return_value = User(
            "Oscar",
            "Password123",
            "Employee",
            1
        )

        response = self.client.post(
            "/users/login",
            json={
                "username": "Oscar",
                "password": "Password123"
            }
        )

        assert response.status_code == 200

        data = response.get_json()

        assert data["username"] == "Oscar"
        assert data["id"] == 1

    @patch("app.users.get_from_username_password")
    def test_login_invalid_credentials(self, mock_login):
        mock_login.return_value = None

        response = self.client.post(
            "/users/login",
            json={
                "username": "Ghost",
                "password": "WrongPassword"
            }
        )

        assert response.status_code == 404

    @patch("app.users.remove")
    @patch("app.users.get_from_id")
    def test_delete_user_success(self, mock_get, mock_remove):
        mock_get.return_value = User(
            "Oscar",
            "Password123",
            "Employee",
            1
        )

        response = self.client.delete("/users/1")

        assert response.status_code == 201
        mock_remove.assert_called_once_with(1)

    @patch("app.users.get_from_id")
    def test_delete_user_not_found(self, mock_get):
        mock_get.return_value = None

        response = self.client.delete("/users/999")

        assert response.status_code == 404


class TestExpenseEndpoints:
    def setup_method(self):
        self.client = app.test_client()

    @patch("app.expenses.create")
    def test_create_expense(self, mock_create):
        mock_create.return_value = Expense(
            user_id=7,
            amount=25.50,
            description="Lunch",
            date="2026-07-23",
            id=1
        )

        response = self.client.post(
            "/expenses",
            json={
                "user_id": 7,
                "amount": 25.50,
                "description": "Lunch",
                "date": "2026-07-23"
            }
        )

        assert response.status_code == 201

        data = response.get_json()
        assert data["id"] == 1
        assert data["user_id"] == 7
        assert data["amount"] == 25.50
        assert data["description"] == "Lunch"
        assert data["date"] == "2026-07-23"

    @patch("app.expenses.get_all")
    def test_get_all_expenses(self, mock_get):
        mock_get.return_value = [
            Expense(7, 25.50, "Lunch", "2026-07-23", 1),
            Expense(7, 100.00, "Hotel", "2026-07-24", 2)
        ]

        response = self.client.get("/expenses")

        assert response.status_code == 200

        data = response.get_json()

        assert len(data) == 2
        assert data[0]["id"] == 1
        assert data[0]["description"] == "Lunch"
        assert data[1]["id"] == 2
        assert data[1]["description"] == "Hotel"

    @patch("app.expenses.get_from_id")
    def test_get_expense_by_id_success(self, mock_get):
        mock_get.return_value = Expense(
            7,
            25.50,
            "Lunch",
            "2026-07-23",
            1
        )

        response = self.client.get("/expenses/1")

        assert response.status_code == 200

        data = response.get_json()

        assert data["id"] == 1
        assert data["user_id"] == 7
        assert data["description"] == "Lunch"

    @patch("app.expenses.get_from_id")
    def test_get_expense_by_id_not_found(self, mock_get):
        mock_get.return_value = None

        response = self.client.get("/expenses/999")

        assert response.status_code == 404

    @patch("app.expenses.edit")
    @patch("app.expenses.get_from_id")
    def test_edit_expense_success(self, mock_get, mock_edit):
        mock_get.return_value = Expense(
            7,
            25.50,
            "Lunch",
            "2026-07-23",
            1
        )

        mock_edit.return_value = Expense(
            7,
            50.00,
            "Dinner",
            "2026-07-24",
            1
        )

        response = self.client.put(
            "/expenses/1",
            json={
                "user_id": 7,
                "amount": 50.00,
                "description": "Dinner",
                "date": "2026-07-24"
            }
        )

        assert response.status_code == 201

        data = response.get_json()

        assert data["id"] == 1
        assert data["amount"] == 50.00
        assert data["description"] == "Dinner"

    @patch("app.expenses.get_from_id")
    def test_edit_expense_not_found(self, mock_get):
        mock_get.return_value = None

        response = self.client.put(
            "/expenses/999",
            json={
                "user_id": 7,
                "amount": 50.00,
                "description": "Dinner",
                "date": "2026-07-24"
            }
        )

        assert response.status_code == 404

    @patch("app.expenses.get_from_id")
    @patch("app.expenses.edit")
    def test_edit_expense_failed(self, mock_edit, mock_get):
        expense = MagicMock()
        mock_get.return_value = expense

        mock_edit.return_value = None

        response = self.client.put(
            "/expenses/999",
            json={
                "user_id": 7,
                "amount": 50.00,
                "description": "Dinner",
                "date": "2026-07-24"
            }
        )

        assert response.status_code == 404

    @patch("app.expenses.remove")
    @patch("app.expenses.get_from_id")
    def test_delete_expense_success(self, mock_get, mock_remove):
        mock_get.return_value = Expense(
            7,
            25.50,
            "Lunch",
            "2026-07-23",
            1
        )

        response = self.client.delete("/expenses/1")

        assert response.status_code == 201

        mock_remove.assert_called_once_with(1)

    @patch("app.expenses.get_from_id")
    def test_delete_expense_not_found(self, mock_get):
        mock_get.return_value = None

        response = self.client.delete("/expenses/999")

        assert response.status_code == 404

    @patch("app.expenses.get_all_by_user")
    def test_get_expenses_by_user(self, mock_get):
        mock_get.return_value = [
            Expense(7, 25.50, "Lunch", "2026-07-23", 1),
            Expense(7, 30.00, "Dinner", "2026-07-24", 2)
        ]

        response = self.client.get("/expenses/user/7")

        assert response.status_code == 200

        data = response.get_json()

        assert len(data) == 2
        assert data[0]["user_id"] == 7
        assert data[1]["description"] == "Dinner"

    @patch("app.expenses.get_all_non_pending_user")
    def test_get_expense_history(self, mock_get):
        mock_get.return_value = [
            Expense(7, 25.50, "Lunch", "2026-07-23", 1)
        ]

        response = self.client.get("/expenses/user/7/history")

        assert response.status_code == 200

        data = response.get_json()

        assert len(data) == 1
        assert data[0]["id"] == 1
        assert data[0]["description"] == "Lunch"