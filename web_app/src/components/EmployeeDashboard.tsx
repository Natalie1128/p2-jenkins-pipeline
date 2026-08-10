import { useEffect, useState } from 'react'
import { employeeApi } from '../api/employeeApi'
import type {
    Approval,
  Expense,
  Review,
} from '../types/models'
import ExpenseTable from './ExpenseTable'
import SubmitExpense from './SubmitExpense'
import EditExpense from './EditExpense'
import { ReviewModal } from './ReviewModal'
import { LogoutButton } from './LogoutButton'
import { useNavigate } from 'react-router-dom'

type FilterMode = 'ALL' | 'HISTORY'

export default function EmployeeDashboard() {
    const savedUser = localStorage.getItem('user')

    const user = savedUser
    ? JSON.parse(savedUser)
    : null

  const [expenses, setExpenses] =
    useState<Expense[]>([])

  const [filterMode, setFilterMode] =
    useState<FilterMode>('ALL')

  const [page, setPage] = useState(1)

  const [showSubmit, setShowSubmit] =
    useState(false)

    const [expenseToEdit, setExpenseToEdit] =
            useState<Expense | null> (null)

    const [expenseToReview, setExpenseToReview] =
        useState<Review | null> (null)

    const navigate = useNavigate()
    
    useEffect(()=>{
        if(user.role != "Employee"){
            navigate('/manager', { replace: true })
        }
    }, [])
  const ITEMS_PER_PAGE = 5

    const handleEdit = (expense: Expense) => {
        setExpenseToEdit(expense)
    }

    const handleReview = (expense: Expense, approval: Approval) => {
        const review = {
                approval_id: approval.id,
                user_id: expense.user_id,
                expense_id: expense.id,
                amount: expense.amount,
                description: expense.description,
                date: expense.date,
                status: approval.status,
                reviewer: approval.reviewer!,
                comment: approval.comment!,
                review_date: approval.review_date!
        }

        setExpenseToReview(review)
    }

    const onUpdated = (expense: Expense) => {
        setExpenses(expenses.map((e: Expense) => 
            e.id == expense.id ?
            expense : e
        ))
        setExpenseToEdit(null)
    }
  async function loadExpenses() {
    try {
      const data =
        filterMode === 'HISTORY'
          ? await employeeApi.getMyExpenseHistory(
              user.id
            )
          : await employeeApi.getMyExpenses(
              user.id
            )

      setExpenses(data.sort((a, b) => b.id - a.id))
    } catch (error) {
      console.error(error)
      setExpenses([])
    }
  }

  useEffect(() => {
    loadExpenses()
  }, [user.id, filterMode])

  const totalPages = Math.max(
    1,
    Math.ceil(
      expenses.length / ITEMS_PER_PAGE
    )
  )

  const startIndex =
    (page - 1) * ITEMS_PER_PAGE

  const visibleExpenses =
    expenses.slice(
      startIndex,
      startIndex + ITEMS_PER_PAGE
    )

  function handleExpenseCreated() {
    setShowSubmit(false)
    loadExpenses()
  }

  const handleExitReview = () => {
    setExpenseToReview(null)
  }

  return (
    <div className="min-h-screen bg-slate-100">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-5">
          <div>
            <h1 className="text-xl font-bold text-slate-900">
              Employee Expense Portal
            </h1>

            <p className="text-sm text-slate-500">
              Welcome back, {user.username}
            </p>
          </div>

          <LogoutButton />
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-6 py-8">
        {showSubmit ? (
          <SubmitExpense
            user={user}
            onCreated={handleExpenseCreated}
            onCancel={() =>
              setShowSubmit(false)
            }
          />
        ) : expenseToEdit ? (
        <EditExpense
            user={user}
            onUpdated={onUpdated}
            onCancel={() =>
                setExpenseToEdit(null)
            }
            expense={expenseToEdit}
        />
        ) : (
          <>
            <div className="mb-8">
              <h2 className="text-3xl font-bold text-slate-900">
                My Expenses
              </h2>
            </div>

            <div className="mb-6 grid gap-4 sm:grid-cols-2">
              <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
                <p className="text-sm font-medium text-slate-500">
                  {filterMode === 'ALL'
                    ? 'Total Expenses'
                    : 'Expense History'}
                </p>

                <p className="mt-2 text-3xl font-bold text-slate-900">
                  {expenses.length}
                </p>
              </div>
            </div>

            <div className="mb-6 flex flex-wrap gap-2 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
              <button
                onClick={() => {
                  setShowSubmit(true)
                }}
                className="rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white hover:bg-blue-700"
              >
                + Submit New Expense
              </button>

              <button
                onClick={() => {
                  setFilterMode('ALL')
                  setPage(1)
                }}
                className={`rounded-lg px-4 py-2.5 text-sm font-semibold ${
                  filterMode === 'ALL'
                    ? 'bg-slate-900 text-white'
                    : 'border border-slate-300 text-slate-700'
                }`}
              >
                All Expenses
              </button>

              <button
                onClick={() => {
                  setFilterMode('HISTORY')
                  setPage(1)
                }}
                className={`rounded-lg px-4 py-2.5 text-sm font-semibold ${
                  filterMode === 'HISTORY'
                    ? 'bg-slate-900 text-white'
                    : 'border border-slate-300 text-slate-700'
                }`}
              >
                History
              </button>
            </div>

            <ExpenseTable
              expenses={visibleExpenses}
              user={user}
              onChanged={loadExpenses}
              onEdit={handleEdit}
              onReview={handleReview}
            />

            <div className="mt-6 flex items-center justify-between rounded-2xl border border-slate-200 bg-white px-6 py-4 shadow-sm">
              <button
                disabled={page === 1}
                onClick={() =>
                  setPage(page - 1)
                }
                className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold disabled:opacity-40"
              >
                Previous
              </button>

              <span className="text-sm text-slate-600">
                Page {page} of {totalPages}
              </span>

              <button
                disabled={page === totalPages}
                onClick={() =>
                  setPage(page + 1)
                }
                className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold disabled:opacity-40"
              >
                Next
              </button>
            </div>
          </>
        )}
      </main>
      {expenseToReview && <ReviewModal isManager={false} review={expenseToReview} onClose={handleExitReview}/>}
    </div>
  )
}