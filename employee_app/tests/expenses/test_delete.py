from unittest.mock import patch, MagicMock
from controllers.expenses import remove

class TestDelete:

    @patch("controllers.expenses.get_connection")
    def test_delete_expense(self, mock_get_connection):
        mock_conn = MagicMock()
        mock_get_connection.return_value = mock_conn

        remove(1)

        sql, params = mock_conn.execute.call_args[0]
        assert "DELETE FROM expenses WHERE id = ?" in sql
        assert params == (1,)

        mock_conn.commit.assert_called_once()
        mock_conn.close.assert_called_once()


