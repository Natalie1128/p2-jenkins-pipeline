from db import get_connection
from db import init_db

def seed(conn):
    cursor = conn.execute("SELECT COUNT(*) FROM users")
    if cursor.fetchone()[0] > 0:
        #print("exist")
        return

    users = [
        (1, "alice", "password123", "Employee"),
        (2, "bob", "password123", "Employee"),
        (3, "carla", "password123", "Employee"),
        (4, "manager", "admin123", "Manager"),
    ]

    expenses = [
        (1, 1, 42.75, "Client lunch", "2026-06-01"),
        (2, 1, 180.00, "Conference registration", "2026-06-04"),
        (3, 2, 65.50, "Office supplies", "2026-06-05"),
        (4, 2, 320.00, "Hotel stay", "2026-06-08"),
        (5, 3, 28.25, "Taxi fare", "2026-06-10"),
        (1+5, 1, 42.75, "Client lunch", "2026-06-11"),
        (2+5, 1, 180.00, "Conference registration", "2026-06-12"),
        (3+5, 2, 65.50, "Office supplies", "2026-06-13"),
        (4+5, 2, 320.00, "Hotel stay", "2026-06-13"),
        (5+5, 3, 28.25, "Taxi fare", "2026-06-13"),
        (1+5+5, 1, 42.75, "Client lunch", "2026-06-15"),
        (2+5+5, 1, 180.00, "Conference registration", "2026-06-17"),
        (3+5+5, 2, 65.50, "Office supplies", "2026-06-20"),
        (4+5+5, 2, 320.00, "Hotel stay", "2026-06-23"),
        (5+5+5, 3, 28.25, "Taxi fare", "2026-06-23"),
    ]

    approvals = [
        (1, 1, "approved", 4, "Approved for client meeting.", "2026-06-02"),
        (2, 2, "pending", None, None, None),
        (3, 3, "denied", 4, "Receipt was missing.", "2026-06-06"),
        (4, 4, "pending", None, None, None),
        (5, 5, "approved", 4, "Approved travel expense.", "2026-06-11"),
        (1+5, 1+5, "approved", 4, "Approved for client meeting.", "2026-06-13"),
        (2+5, 2+5, "pending", None, None, None),
        (3+5, 3+5, "denied", 4, "Receipt was missing.", "2026-06-14"),
        (4+5, 4+5, "pending", None, None, None),
        (5+5, 5+5, "approved", 4, "Approved travel expense.", "2026-06-18"),
        (1+10, 1+10, "approved", 4, "Approved for client meeting.", "2026-06-18"),
        (2+10, 2+10, "pending", None, None, None),
        (3+10, 3+10, "denied", 4, "Receipt was missing.", "2026-06-24"),
        (4+10, 4+10, "pending", None, None, None),
        (5+10, 5+10, "approved", 4, "Approved travel expense.", "2026-06-24"),
    ]

    conn.executemany(
        """
        INSERT INTO users (id, username, password, role)
        VALUES (?, ?, ?, ?)
        """,
        users,
    )

    conn.executemany(
        """
        INSERT INTO expenses (id, user_id, amount, description, date)
        VALUES (?, ?, ?, ?, ?)
        """,
        expenses,
    )

    conn.executemany(
        """
        INSERT INTO approvals (id, expense_id, status, reviewer, comment, review_date)
        VALUES (?, ?, ?, ?, ?, ?)
        """,
        approvals,
    )
    conn.commit()
    conn.close()

init_db()
seed(get_connection())
