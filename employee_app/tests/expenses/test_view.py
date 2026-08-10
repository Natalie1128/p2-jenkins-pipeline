from unittest.mock import patch, MagicMock
from controllers.expenses import get_all
from controllers.expenses import get_all_by_user
from controllers.expenses import get_all_non_pending_user
from controllers.expenses import get_from_id


class TestView:

    @patch("controllers.expenses.get_connection")
    def test_get_all(self, mock_get_connection):
        mock_conn = MagicMock()
        mock_get_connection.return_value = mock_conn

        fake_rows = [
            (1, 1, 50, "Description 1", "2026-07-31"),
            (2, 1, 75, "Description 2", "2026-07-31"),
            (3, 2, 25, "Description 3", "2026-07-31")
        ]
        mock_conn.execute.return_value.fetchall.return_value = fake_rows

        result = get_all()

        assert len(result) == 3
        
        assert result[0].id == 1
        assert result[0].user_id == 1
        assert result[0].amount == 50
        assert result[0].description == "Description 1"
        assert result[0].date == "2026-07-31"

        assert result[1].id == 2
        assert result[1].user_id == 1
        assert result[1].amount == 75
        assert result[1].description == "Description 2"
        assert result[1].date == "2026-07-31"

        assert result[2].id == 3
        assert result[2].user_id == 2
        assert result[2].amount == 25
        assert result[2].description == "Description 3"
        assert result[2].date == "2026-07-31"

    @patch("controllers.expenses.get_connection")
    def test_get_all_by_user(self, mock_get_connection):
        mock_conn = MagicMock()
        mock_get_connection.return_value = mock_conn

        fake_rows = [
            (1, 1, 50, "Description 1", "2026-07-31"),
            (2, 1, 75, "Description 2", "2026-07-31")
        ]
        mock_conn.execute.return_value.fetchall.return_value = fake_rows

        result = get_all_by_user(1)

        assert mock_conn.execute.call_args[0][1] == (1,)

        assert len(result) == 2

        assert result[0].id == 1
        assert result[0].user_id == 1
        assert result[0].amount == 50
        assert result[0].description == "Description 1"
        assert result[0].date == "2026-07-31"

        assert result[1].id == 2
        assert result[1].user_id == 1
        assert result[1].amount == 75
        assert result[1].description == "Description 2"
        assert result[1].date == "2026-07-31"
    
    @patch("controllers.expenses.get_connection")
    def test_get_all_non_pending_user(self, mock_get_connection):
        mock_conn = MagicMock()
        mock_get_connection.return_value = mock_conn

        fake_rows = [
            {
                "id": 1,
                "user_id": 1,
                "amount": 50,
                "description": "Description 1",
                "date": "2026-07-31"
            },
            {
                "id": 2,
                "user_id": 1,
                "amount": 75,
                "description": "Description 2",
                "date": "2026-07-31"
            }
        ]
        mock_conn.execute.return_value.fetchall.return_value = fake_rows
        
        result = get_all_non_pending_user(1)

        assert mock_conn.execute.call_args[0][1] == (1,)
        assert len(result) == 2

        assert result[0].id == 1
        assert result[0].user_id == 1
        assert result[0].amount == 50
        assert result[0].description == "Description 1"
        assert result[0].date == "2026-07-31"

        assert result[1].id == 2
        assert result[1].user_id == 1
        assert result[1].amount == 75
        assert result[1].description == "Description 2"
        assert result[1].date == "2026-07-31"

    @patch("controllers.expenses.get_connection")
    def test_get_from_id(self, mock_get_connection):
        mock_conn = MagicMock()
        mock_get_connection.return_value = mock_conn

        fake_row = (1, 1, 50, "Description 1", "2026-07-31")
        mock_conn.execute.return_value.fetchone.return_value = fake_row

        result = get_from_id(1)

        assert mock_conn.execute.call_args[0][1] == (1,)

        assert result.id == 1
        assert result.user_id == 1
        assert result.amount == 50
        assert result.description == "Description 1"
        assert result.date == "2026-07-31"

    @patch("controllers.expenses.get_connection")
    def test_getFromId_noneRowShouldReturnNone(self, mock_get_connection):
        mock_conn = MagicMock()
        mock_get_connection.return_value = mock_conn

        mock_conn.execute.return_value.fetchone.return_value = None

        result = get_from_id(1)

        assert mock_conn.execute.call_args[0][1] == (1,)

        assert result is None
