import sys
from pathlib import Path
from unittest.mock import MagicMock

import pytest

ROOT = Path(__file__).resolve().parents[1]
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))


@pytest.fixture
def mock_get_connection(monkeypatch):
    mock_conn = MagicMock()
    mock_get_connection_fn = MagicMock(return_value=mock_conn)
    monkeypatch.setattr("controllers.users.get_connection", mock_get_connection_fn)
    return mock_get_connection_fn
