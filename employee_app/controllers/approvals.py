from db.db import get_connection
from  models.approvals import Approval

def create(approval:Approval):
    conn = get_connection()
    cursor = conn.execute(
        """
        INSERT INTO approvals (expense_id, status, reviewer, comment, review_date)
        VALUES (?, ?, ?, ?, ?)
        """,
        (approval.expense_id, approval.status, approval.reviewer, approval.comment, approval.review_date)
    )

    approval.id = cursor.lastrowid
    conn.commit()
    conn.close()

    return approval

def get_all():
    conn = get_connection()
    cursor = conn.execute(
        """
        SELECT * FROM approvals
        """
    )

    rows = cursor.fetchall()
    approvals = []

    for row in rows:
        approvals.append(Approval(
            id= row[0],
            expense_id=row[1],
            status=row[2],
            reviewer=row[3],
            comment=row[4],
            review_date=row[5]
        ))

    conn.close()

    return approvals

def get_from_id(id:int):
    conn = get_connection()
    cursor = conn.execute(
    """
    SELECT * FROM approvals WHERE id = ?
    """, 
    (id,)
    )

    row = cursor.fetchone()
    conn.close()

    if row is None:
        return None

    return Approval(
            id= row[0],
            expense_id=row[1],
            status=row[2],
            reviewer=row[3],
            comment=row[4],
            review_date=row[5]
    )


def get_from_expenseid(id:int):
    conn = get_connection()
    cursor = conn.execute(
    """
    SELECT a.* FROM approvals a
        JOIN expenses e ON e.id = a.expense_id
        WHERE e.id = ?
    """, 
    (id,)
    )

    row = cursor.fetchone()
    conn.close()

    if row is None:
        return None

    return Approval(
            id= row[0],
            expense_id=row[1],
            status=row[2],
            reviewer=row[3],
            comment=row[4],
            review_date=row[5]
    )

# implemented for API testing cleanup - will not be used in application
def remove(id:int):
    conn = get_connection()
    conn.execute(
        """
        DELETE FROM approvals WHERE id = ?
        """,
        (id,)
    )

    conn.commit()
    conn.close()