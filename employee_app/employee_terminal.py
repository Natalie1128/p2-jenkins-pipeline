from models.users import User

import os
import requests
import getpass
import math
from datetime import datetime
from prettytable import PrettyTable

#BASE_URL = "http://100.84.247.62:8080"
BASE_URL = "http://127.0.0.1:8080"

def clear_screen():
    os.system('cls' if os.name == 'nt' else 'clear')

def get_expense_status(expense_id):
    response = requests.get(f"{BASE_URL}/approvals/expense/{expense_id}")
    if response.status_code == 200:
        return response.json().get('status', 'Unknown')
    return "Unknown"

def submit_expense(user:User):
    clear_screen()
    print(f"{'='*25}\nExpense Report Submission\n{'='*25}\n")
    while True:
        amount = input("Please enter the amount of the expense (or q to exit): $")
        if amount.strip().lower() == 'q':
            return
        try:
            amount = int(amount)
            break
        except ValueError:
            print("\nInvalid input. Please enter a valid number.")
        
    description = input("Please enter the description of the expense: ")
    while True:
        date = input("Please enter the date of the expense (YYYY-MM-DD): ").strip()
        try:
            parsed_date = datetime.strptime(date, "%Y-%m-%d").date()
            if parsed_date > datetime.today().date():
                print("\nInvalid date. The expense date cannot be in the future.\n")
                continue
            break
        except ValueError:
            print("\nInvalid format. Please use YYYY-MM-DD.\n")

    payload = {
        "user_id": user.id,
        "amount": amount,
        "description": description,
        "date": date
    }

    try:
        expense = (requests.post(f"{BASE_URL}/expenses", json=payload)).json()
        approval_payload = {
            "expense_id": expense['id'],
            "status": "pending",
            "reviewer": None,
            "comment": "",
            "review_date": ""
        }
        requests.post(f"{BASE_URL}/approvals", json=approval_payload)
        print("\nExpense submitted successfully!")
        input("\nPress Enter to continue...")
    except requests.exceptions.ConnectionError:
        print("\nUnable to connect to the server. Please try again later.")
        input("\nPress Enter to continue...")

    except requests.exceptions.RequestException:
        print("\nA network error occurred while submitting the expense.")
        input("\nPress Enter to continue...")

    except Exception:
        print("\nAn unexpected error occurred while submitting the expense.")
        input("\nPress Enter to continue...")
        

def edit_expense(user:User):
    clear_screen()
    print(f"{'='*25}\nEdit Expense Report\n{'='*25}\n")
    
    user_input = input("Please enter the expense id you would like to edit (press q to exit): ").strip().lower()
    if user_input == 'q':
        return
    try:
        expense_id = int(user_input)
    except ValueError:
        print("\nInvalid input. Please enter a valid number.")
        input("\nPress Enter to continue...")
        return

    expense_response = requests.get(f"{BASE_URL}/expenses/{expense_id}")
    if expense_response.status_code != 200:
        print("Expense does not exist.\n")
        input("Press Enter to continue...")
        return
        
    expense = expense_response.json()
    status = get_expense_status(expense_id)

    if expense['user_id'] != user.id:
        print("Invalid operation. You can only edit your own expense reports.\n")
        input("Press Enter to continue...")
        return
    elif status != "pending":
        print("Invalid operation. You can only edit pending expense reports.\n")
        input("Press Enter to continue...")
        return

    while True:
        amount = input(f"Please enter the new amount (Current: ${expense['amount']}) [Leave blank to keep]: ")
        try:
            if amount == "":
                break
            expense['amount'] = int(amount)
            break
        except ValueError:
            print("\nInvalid input. Please enter a valid number.")

    description = input(f"Please enter the new description (Current: {expense['description']}) [Leave blank to keep]: ").strip()
    expense['description'] = description or expense['description']

    while True:
        date_input = input(f"Please enter the new date (YYYY-MM-DD) (Current: {expense['date']}) [Leave blank to keep]: ").strip()
        if date_input == "":
            break
        try:
            parsed_date = datetime.strptime(date_input, "%Y-%m-%d").date()
            if parsed_date > datetime.today().date():
                print("\nInvalid date. The expense date cannot be in the future.\n")
                continue
            expense['date'] = date_input
            break
        except ValueError:
            print("\nInvalid format. Please use YYYY-MM-DD.\n")
    
    payload = {
        "user_id": user.id,
        "amount": expense['amount'],
        "description": expense['description'],
        "date": expense['date']
    }

    response = requests.put(f"{BASE_URL}/expenses/{expense_id}", json=payload)
    if response.status_code == 200:
        print("\nExpense updated successfully!")
    else:
        print("\nExpense could not be updated.")
    input("\nPress Enter to continue...")


def delete_expense(user:User):
    clear_screen()
    print(f"{'='*25}\nRemove Expense Report\n{'='*25}\n")
    
    user_input = input("Please enter the expense id you would like to remove (press q to exit): ").strip().lower()
    if user_input == 'q':
        return
    try:
        expense_id = int(user_input)
    except ValueError:
        print("\nInvalid input. Please enter a valid number.")
        input("\nPress Enter to continue...")
        return

    expense_response = requests.get(f"{BASE_URL}/expenses/{expense_id}")
    if expense_response.status_code != 200:
        print("Expense does not exist.\n")
        input("Press Enter to continue...")
        return
        
    expense = expense_response.json()
    status = get_expense_status(expense_id)

    if expense['user_id'] != user.id:
        print("Invalid operation. You can only delete your own expense reports.\n")
    elif status != "pending":
        print("Invalid operation. You can only delete pending expense reports.\n")
    else:
        delete_response = requests.delete(f"{BASE_URL}/expenses/{expense_id}")
        if delete_response.status_code == 200:
            print("\nExpense deleted successfully!")
        else:
            print("\nFailed to delete expense.")
            
    input("\nPress Enter to continue...")



def dashboard(user:User):
    running_dash = True

    curPage = 1
    filter_mode = "ALL"

    while running_dash:
        clear_screen()
        print(f"\nWelcome Employee {user.username.capitalize()}!")
        print(f"Active Filter: {filter_mode}")

        if filter_mode == "HISTORY":
            response = requests.get(f"{BASE_URL}/expenses/user/{user.id}/history")
        else:
            response = requests.get(f"{BASE_URL}/expenses/user/{user.id}")
        
        if response.status_code == 200:
            expenses = response.json()
        else:
            expenses = []
        
        total_items = len(expenses)
        total_pages = max(1, math.ceil(total_items / 5))

        if curPage > total_pages:
            curPage = total_pages
        print(f"\nSubmitted Expenses (Page {curPage}/{total_pages})")

        table = PrettyTable()
        table.field_names = ["ID", "Amount", "Description", "Date", "Status"]
        table.align["Description"] = "l"

        start_idx = (curPage - 1) * 5
        end_idx = start_idx + 5
        page_items = expenses[start_idx:end_idx]

        if page_items:
            for expense in page_items:
                status = get_expense_status(expense['id'])
                table.add_row([
                    expense['id'],
                    f"${expense['amount']}",
                    expense['description'],
                    expense['date'],
                    status.upper()
                ])
        
        print(table)
        
        print("\nN - Next Page")
        print("P - Previous Page")
        print("S - Submit New Expense")
        print("E - Edit Pending Expense")
        print("D - Delete Expense")
        print("F - Filter")
        print("Q - Back")

        user_input = input("\nPlease select an option: ").strip().upper()
        
        if user_input == 'Q':
            clear_screen()
            running_dash = False
        elif user_input == 'N':
            if curPage < total_pages:
                curPage += 1
        elif user_input == 'P':
            if curPage > 1:
                curPage -= 1
        elif user_input == 'S':
            submit_expense(user)
        elif user_input == 'E':
            edit_expense(user)
        elif user_input == 'D':
            delete_expense(user)
        elif user_input == 'F':
            clear_screen()
            print(f"{'='*25}\nChange Filter\n{'='*25}\n")
            while(True):
                user_input = input("Please type 1 to view all expenses or 2 to view non-pending expenses: ").strip()
                try:
                    user_command = int(user_input)
                    if user_command == 1:
                        filter_mode = "ALL"
                        curPage = 1
                        break
                    elif user_command == 2:
                        filter_mode = "HISTORY"
                        curPage = 1
                        break
                    else:
                        print("\nInvalid input. Please enter a valid command.")
                except ValueError:
                    print("\nInvalid input. Please enter a valid command.")
            

def main():
    clear_screen()
    print("Welcome to the Employee App!")

    running_main = True

    while running_main:
        user_input = input("\nPlease type 1 to login or q to exit: ").strip().lower()
        if user_input == 'q':
            print("Goodbye!")
            running_main = False
            continue
        try:
            user_command = int(user_input)
            if user_command != 1:
                clear_screen()
                print("Invalid operation. Please enter 1 or q.")
                continue
        except ValueError as e:
            clear_screen()
            print("Invalid input. Please enter 1 or q.")
            continue
        
        if user_command == 1:
            
            username = input("Enter your username: ")
            password = getpass.getpass(prompt="Enter your password: ")
            package = {
                "username": username,
                "password": password
            }
            login_request = requests.post(f"{BASE_URL}/users/login", json=package)

            if login_request.status_code == 200:
                raw_user_data = login_request.json()
                if raw_user_data['role'] == "Manager":
                    clear_screen()
                    print("Only employee logins permitted")
                    continue
                logged_in_user = User(
                    id=raw_user_data['id'],
                    username=raw_user_data['username'],
                    password=raw_user_data['password'],
                    role=raw_user_data['role']
                )
                clear_screen()
                dashboard(logged_in_user)
            else:
                clear_screen()
                print("Username or password not valid, Please try again!")
    

if __name__ == "__main__":
    main()
