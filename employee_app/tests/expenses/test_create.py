from unittest.mock import MagicMock, patch
from controllers.expenses import create
from models.expenses import Expense
from utils.exceptions.database_exception import DatabaseException
import sqlite3
import pytest

class TestCreate:
    @patch("controllers.expenses.get_connection")
    def test_create_validShouldReturnApprovals(self, mock_get_connection):
        conn = MagicMock()
        cursor = MagicMock()

        mock_get_connection.return_value.__enter__.return_value = conn
        conn.execute.return_value = cursor
        cursor.lastrowid = 5

        expense = Expense(1, 10, "description", "may15")
        resulting_expense = create(expense)

        assert resulting_expense.__str__() == (f"Expense #5: $10 for description "
        f"on may15")

    @patch("controllers.expenses.get_connection")
    def test_create_invalidShouldRaiseDatabaseError(self, mock_get_connection):
        conn = MagicMock()

        mock_get_connection.return_value.__enter__.return_value = conn
        conn.execute.side_effect = sqlite3.IntegrityError()

        expense = Expense(1, 1, "description", "date")

        with pytest.raises(DatabaseException) as e:
            create(expense)
        
        assert str(e.value) == "Contraints violated"


    @patch("controllers.expenses.get_connection")
    def test_create_failedConnectionShouldRaiseDatabaseError(self, mock_get_connection):
        mock_get_connection.side_effect = sqlite3.DatabaseError

        expense = Expense(1, 1, "description", "date")

        with pytest.raises(DatabaseException) as e:
            create(expense)

        assert str(e.value) == "Database execution failed"

        




