from unittest.mock import patch, MagicMock
from controllers.users import get_from_username_password


class TestLogin:

    @patch("controllers.users.get_connection")
    def test_login_valid_credentials_returns_user(self, mock_get_connection):
        mock_conn = MagicMock()
        mock_get_connection.return_value = mock_conn

        fake_row = (1, "alice", "password123", "Employee")
        mock_conn.execute.return_value.fetchone.return_value = fake_row

        result = get_from_username_password("alice", "password123")

        assert result is not None
        assert result.id == 1
        assert result.username == "alice"
        assert result.role == "Employee"
    
    @patch("controllers.users.get_connection")
    def test_login_invalid_creditials_returns_none(self, mock_get_connection):
        mock_conn = MagicMock()
        mock_get_connection.return_value = mock_conn

        fake_row = None
        mock_conn.execute.return_value.fetchone.return_value = fake_row

        result = get_from_username_password("ghost", "wrongpassword")

        assert result is None
