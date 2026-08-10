from unittest.mock import patch, MagicMock
from controllers.approvals import create, get_all, get_from_id, get_from_expenseid, remove
from models.approvals import Approval

class TestCreateApproval:
    @patch("controllers.approvals.get_connection")
    def test_create_approval(self, mock_get_connection):
        conn = MagicMock()
        cursor = MagicMock()
        mock_get_connection.return_value = conn
        conn.execute.return_value = cursor
        cursor.lastrowid = 99

        approval = Approval(40)

        result = create(approval)

        assert result.id == 99

        sql, params = conn.execute.call_args.args

        assert "INSERT INTO approvals" in sql
        assert params == (40, "pending", None, None, None)

class TestGetAllApprovals:
    @patch("controllers.approvals.get_connection")
    def test_get_all_approvals(self, mock_get_connection):
        conn = MagicMock()
        mock_get_connection.return_value = conn

        conn.execute.return_value.fetchall.return_value = [
            (1, 40, "approved", 7, "Looks good", "2026-07-23"),
            (2, 41, "pending", None, None, None),
            (3, 42, "denied", 9, "Too much money", "2026-07-23")
        ]

        result = get_all()
        assert len(result) == 3

        assert result[0].id == 1
        assert result[0].expense_id == 40
        assert result[0].status == "approved"
        assert result[0].reviewer == 7
        assert result[0].comment == "Looks good"
        assert result[0].review_date == "2026-07-23"

        assert result[1].id == 2
        assert result[1].expense_id == 41
        assert result[1].status == "pending"
        assert result[1].reviewer == None
        assert result[1].comment == None
        assert result[1].review_date == None

        assert result[2].id == 3
        assert result[2].expense_id == 42
        assert result[2].status == "denied"
        assert result[2].reviewer == 9
        assert result[2].comment == "Too much money"
        assert result[2].review_date == "2026-07-23"


class TestGetFromId:
    @patch("controllers.approvals.get_connection")
    def test_get_from_valid_id(self, mock_get_connection):
        conn = MagicMock()
        mock_get_connection.return_value = conn

        conn.execute.return_value.fetchone.return_value = (1, 40, "approved", 7, "Looks good", "2026-07-23")
        result = get_from_id(1)

        assert result.id == 1
        assert result.expense_id == 40
        assert result.status == "approved"
        assert result.reviewer == 7
        assert result.comment == "Looks good"
        assert result.review_date == "2026-07-23"

    @patch("controllers.approvals.get_connection")
    def test_get_from_valid_id_str(self, mock_get_connection):
        conn = MagicMock()
        mock_get_connection.return_value = conn

        conn.execute.return_value.fetchone.return_value = (1, 40, "approved", 7, "Looks good", "2026-07-23")
        result = get_from_id(1)

        assert str(result) == (
        "Approval #" + str(result.id) +
        ": expense #" + str(result.expense_id) +
        " is " + result.status +
        " by reviewer #" + str(result.reviewer) +
        " on " + str(result.review_date) +
        ". Comment: " + str(result.comment)
    )

    @patch("controllers.approvals.get_connection")
    def test_get_from_invalid_id(self, mock_get_connection):
        conn = MagicMock()
        mock_get_connection.return_value = conn

        conn.execute.return_value.fetchone.return_value = None
        result = get_from_id(10021)

        assert result is None

class TestGetFromExpenseId:
        @patch("controllers.approvals.get_connection")
        def test_get_from_valid_expense_id(self, mock_get_connection):
            conn = MagicMock()
            mock_get_connection.return_value = conn
    
            conn.execute.return_value.fetchone.return_value = (1, 40, "approved", 7, "Looks good", "2026-07-23")
            result = get_from_expenseid(1)

            assert result.id == 1
            assert result.expense_id == 40
            assert result.status == "approved"
            assert result.reviewer == 7
            assert result.comment == "Looks good"
            assert result.review_date == "2026-07-23"

        @patch("controllers.approvals.get_connection")
        def test_get_from_invalid_expense_id(self, mock_get_connection):
            conn = MagicMock()
            mock_get_connection.return_value = conn
    
            conn.execute.return_value.fetchone.return_value = None
            result = get_from_expenseid(10021)

            assert result is None

class TestRemoveApproval:
    @patch("controllers.approvals.get_connection")
    def test_remove(self, mock_get_connection):
        conn = MagicMock()
        mock_get_connection.return_value = conn

        remove(99)

        sql, params = conn.execute.call_args.args
        
        assert "DELETE FROM approvals WHERE id = ?" in sql
        assert params == (99,)
        