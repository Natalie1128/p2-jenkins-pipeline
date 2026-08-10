from unittest.mock import patch, MagicMock
from controllers.users import get_all
from controllers.users import get_from_id

class TestGetAllUsers:

    @patch("controllers.users.get_connection")
    def test_get_all_returns_users(self, mock_get_connection):
        conn = MagicMock()
        mock_get_connection.return_value = conn

        conn.execute.return_value.fetchall.return_value = [
            (1, "alice", "pw", "Employee"),
            (2, "bob", "pw", "Manager")
        ]

        users = get_all()

        assert len(users) == 2
        assert users[0].username == "alice"
        assert users[1].username == "bob"

        conn.close.assert_called_once()



class TestGetFromId:
    @patch("controllers.users.get_connection")
    def test_get_from_id_found(self, mock_get_connection):
        conn = MagicMock()
        mock_get_connection.return_value = conn

        conn.execute.return_value.fetchone.return_value = (
            1, "alice", "pw", "Employee"
        )

        user = get_from_id(1)

        assert user is not None
        assert user.id == 1
        assert user.username == "alice"

        conn.close.assert_called_once()

    @patch("controllers.users.get_connection")
    def test_get_from_id_found_str(self, mock_get_connection):
        conn = MagicMock()
        mock_get_connection.return_value = conn

        conn.execute.return_value.fetchone.return_value = (
            1, "alice", "pw", "Employee"
        )

        user = get_from_id(1)

        assert user.__str__() == (f"User #{user.id}: {user.username} ({user.role})")

        conn.close.assert_called_once()

    @patch("controllers.users.get_connection")
    def test_get_from_id_not_found(self, mock_get_connection):
        conn = MagicMock()
        mock_get_connection.return_value = conn

        conn.execute.return_value.fetchone.return_value = None

        user = get_from_id(999)

        assert user is None

        conn.close.assert_called_once()