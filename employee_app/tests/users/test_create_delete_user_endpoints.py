import unittest
from unittest.mock import patch
from app import app

class TestUserEndpoints(unittest.TestCase):

    def setUp(self):
        self.client = app.test_client()

    @patch("app.users.create")
    def test_create_user_success(self, mock_create):
        mock_create.return_value = type(
            "User", (),
            {
                "__dict__": {
                    "id": 1,
                    "username": "employeeUsername",
                    "password": "password",
                    "role": "Employee"
                }
            }
        )()

        response = self.client.post(
            "/users",
            json={
                "username": "employeeUsername",
                "password": "password",
                "role": "Employee"
            }
        )

        self.assertEqual(response.status_code, 201)
        self.assertEqual(response.get_json()["username"], "employeeUsername")
        mock_create.assert_called_once()

    @patch("app.users.remove")
    @patch("app.users.get_from_id")
    def test_delete_user_success(self, mock_get, mock_remove):
        mock_get.return_value = object()

        response = self.client.delete("/users/1")

        self.assertEqual(response.status_code, 201)
        mock_remove.assert_called_once_with(1)

    @patch("app.users.get_from_id")
    def test_delete_user_not_found(self, mock_get):
        mock_get.return_value = None

        response = self.client.delete("/users/999")

        self.assertEqual(response.status_code, 404)