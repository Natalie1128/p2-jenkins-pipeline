from controllers import approvals, expenses, users
from models.expenses import Expense
from flask_cors import CORS
from db.db import init_db

from flask import Flask, jsonify, request

app = Flask(__name__)
init_db()

CORS(app)

@app.route('/health', methods=['GET'])
def handle_get_backend_health():
    return jsonify(dict({"status":"ok"})), 200

@app.route('/approvals', methods=['GET'])
def handle_get_all_approvals():
    all_approvals = approvals.get_all()
    dict_list = []
    for a in all_approvals:
        dict_list.append(a.__dict__)
    return jsonify(dict_list), 200

@app.route('/approvals', methods=['POST'])
def handle_create_approval():
    data = request.json
    new_approval = approvals.Approval(
        expense_id=data['expense_id'],
        status=data['status'],
        reviewer=data['reviewer'],
        comment=data['comment'],
        review_date=data['review_date']
    )

    saved_approval = approvals.create(new_approval)
    
    return jsonify(saved_approval.__dict__), 201

@app.route('/approvals/<int:approval_id>', methods=['GET'])
def handle_get_approval_by_id(approval_id):
    approval = approvals.get_from_id(approval_id)
    
    if approval is None:
        return jsonify({"error": f"Approval with ID {approval_id} not found"}), 404
    
    return jsonify(approval.__dict__), 200

@app.route('/approvals/expense/<int:expense_id>', methods=['GET'])
def handle_get_approval_by_expense_id(expense_id):
    approval = approvals.get_from_expenseid(expense_id)
    
    if approval is None:
        return jsonify({"error": f"No approval records found for expense ID {expense_id}"}), 404
    
    return jsonify(approval.__dict__), 200

@app.route('/users', methods=['POST'])
def handle_create_user():
    data = request.json
    new_user = users.User(
        username=data['username'],
        password=data['password'],
        role=data['role']
    )

    saved_user = users.create(new_user)
    
    return jsonify(saved_user.__dict__), 201

@app.route('/users/<int:user_id>', methods=['DELETE'])
def handle_delete_user(user_id):
    existing = users.get_from_id(user_id)
    if existing is None:
        return jsonify({"error": f"User with ID {user_id} not found"}), 404

    users.remove(user_id)
    return jsonify({"message": f"User {user_id} successfully deleted"}), 201

@app.route('/users', methods=['GET'])
def handle_get_all_users():
    all_users = users.get_all()
    dict_list = []
    for user in all_users:
        dict_list.append(user.__dict__)
    return jsonify(dict_list), 200

@app.route('/users/<int:user_id>', methods=['GET'])
def handle_get_user_by_id(user_id):
    user = users.get_from_id(user_id)

    if user is None:
        return jsonify({"error": f"No user records found for user ID {user_id}"}), 404

    return jsonify(user.__dict__), 200

@app.route('/users/login', methods=['POST'])
def handle_login():
    data = request.json
    user = users.get_from_username_password(data['username'], data['password'])

    if user is None:
        return jsonify({"error": f"No user records found with given login"}), 404
    return jsonify(user.__dict__), 200

@app.route('/expenses', methods=['POST'])
def handle_create_expense():
    data = request.json
    new_expense = Expense(
        id=None,
        user_id=data['user_id'],
        amount=data['amount'],
        description=data['description'],
        date=data['date']
    )
    saved_expense = expenses.create(new_expense)
    return jsonify(saved_expense.__dict__), 201

@app.route('/expenses/<int:expense_id>', methods=['PUT'])
def handle_edit_expense(expense_id):
    if expenses.get_from_id(expense_id) is None:
        return jsonify({"error": f"Expense with ID {expense_id} not found"}), 404
    data = request.json
    expense_to_update = Expense(
        id=expense_id,
        user_id=data.get('user_id'), 
        amount=data['amount'],
        description=data['description'],
        date=data['date']
    )
    updated_expense = expenses.edit(expense_to_update)
    if updated_expense is None:
        return jsonify({"error": f"Expense with ID {expense_id} not found"}), 404
        
    return jsonify(updated_expense.__dict__), 201

@app.route('/expenses/<int:expense_id>', methods=['DELETE'])
def handle_delete_expense(expense_id):
    existing = expenses.get_from_id(expense_id)
    if existing is None:
        return jsonify({"error": f"Expense with ID {expense_id} not found"}), 404
        
    expenses.remove(expense_id)
    return jsonify({"message": f"Expense {expense_id} successfully deleted"}), 201

# implemented for API testing cleanup
@app.route('/approvals/<int:approval_id>', methods=['DELETE'])
def handle_delete_approval(approval_id):
    existing = approvals.get_from_id(approval_id)
    if existing is None:
        return jsonify({"error": f"Approval with ID {approval_id} not found"}), 404
    approvals.remove(approval_id)
    return jsonify({"message": f"Approval {approval_id} successfully deleted"}, 201)

@app.route('/expenses', methods=['GET'])
def handle_get_all_expenses():
    all_expenses = expenses.get_all()
    return jsonify([e.__dict__ for e in all_expenses]), 200

@app.route('/expenses/<int:expense_id>', methods=['GET'])
def handle_get_expense_by_id(expense_id):
    expense = expenses.get_from_id(expense_id)
    if expense is None:
        return jsonify({"error": f"Expense with ID {expense_id} not found"}), 404
    return jsonify(expense.__dict__), 200

@app.route('/expenses/user/<int:user_id>', methods=['GET'])
def handle_get_expenses_by_user(user_id):
    user_expenses = expenses.get_all_by_user(user_id)
    return jsonify([e.__dict__ for e in user_expenses]), 200

@app.route('/expenses/user/<int:user_id>/history', methods=['GET'])
def handle_get_non_pending_user_expenses(user_id):
    history_expenses = expenses.get_all_non_pending_user(user_id)
    return jsonify([e.__dict__ for e in history_expenses]), 200

if __name__ == "__main__": # pragma: no cover
    app.run(host='0.0.0.0', port=8080, debug=False)
