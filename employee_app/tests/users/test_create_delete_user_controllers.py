from unittest.mock import MagicMock, patch
from controllers.users import create, remove
from models.users import User


class TestCreateUser:

    @patch("controllers.users.get_connection")
    def test_create_validShouldReturnUser(self, mock_get_connection):
        conn = MagicMock()
        cursor = MagicMock()

        mock_get_connection.return_value = conn
        conn.execute.return_value = cursor
        cursor.lastrowid = 5

        user = User("username", "password", "Employee")

        result = create(user)

        assert result.id == 5
        assert result.username == "username"
        assert result.password == "password"
        assert result.role == "Employee"
        conn.execute.assert_called_once_with(
            """
        INSERT INTO users (username, password, role)
        VALUES (?, ?, ?)
        """,
            ("username", "password", "Employee")
        )


class TestRemoveUser:

    @patch("controllers.users.get_connection")
    def test_remove_valid(self, mock_get_connection):
        conn = MagicMock()
        cursor = MagicMock()

        mock_get_connection.return_value = conn
        conn.cursor.return_value = cursor

        remove(5)

        cursor.execute.assert_called_once_with(
            "DELETE FROM users WHERE id = ?",
            (5,)
        )

        conn.commit.assert_called_once()
        conn.close.assert_called_once()
        
    @patch("controllers.users.get_connection")
    def test_remove_invalid_user(self, mock_get_connection):
        conn = MagicMock()
        cursor = MagicMock()

        mock_get_connection.return_value = conn
        conn.cursor.return_value = cursor
        cursor.rowcount = 0

        result = remove(999)

        assert result == 0
        cursor.execute.assert_called_once()