from db.db import get_connection
from models.users import User

def create(user:User):
    conn = get_connection()
    cursor = conn.execute(
        """
        INSERT INTO users (username, password, role)
        VALUES (?, ?, ?)
        """,
        (user.username, user.password, user.role)
    )

    user.id = cursor.lastrowid
    conn.commit()
    conn.close()

    return user


def get_all():
    conn = get_connection()
    cursor = conn.execute(
        """
        SELECT * FROM users
        """
    )

    rows = cursor.fetchall()
    users = []

    for row in rows:
        users.append(User(
            id=row[0],
            username=row[1],
            password=row[2],
            role=row[3]
        ))

    conn.close()

    return users


def get_from_id(id:int):
    conn = get_connection()
    cursor = conn.execute(
    """
    SELECT * FROM users WHERE id = ?
    """, 
    (id,)
    )

    row = cursor.fetchone()
    conn.close()

    if row is None:
        return None

    return User(
        id=row[0],
        username=row[1],
        password=row[2],
        role=row[3]
    )


def get_from_username_password(username, password):
    conn = get_connection()
    cursor = conn.execute(
    """
    SELECT * FROM users WHERE username = ? AND password = ?
    """, 
    (username,password)
    )

    row = cursor.fetchone()
    conn.close()

    if row is None:
        return None

    return User(
        id=row[0],
        username=row[1],
        password=row[2],
        role=row[3]
    )


def remove(user_id):
    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute(
        "DELETE FROM users WHERE id = ?",
        (user_id,)
    )

    rowcount = cursor.rowcount

    conn.commit()
    conn.close()
    return rowcount
