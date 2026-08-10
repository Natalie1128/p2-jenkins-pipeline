import { useEffect, useState } from 'react'
import { employeeApi } from '../api/employeeApi'
import type {
    Approval,
    Expense,
    User,
} from '../types/models'

interface ExpenseTableProps {
    expenses: Expense[]
    user: User
    onChanged: () => void
    onEdit: (expense: Expense) => void
    onReview: (expense: Expense, approval: Approval) => void
}

export default function ExpenseTable({
    expenses,
    onChanged,
    onEdit,
    onReview,
}: ExpenseTableProps) {
    const [approvals, setApprovals] =
        useState<Record<number, Approval>>({})

    const [loadingId, setLoadingId] =
        useState<number | null>(null)

    const [expenseToDelete, setExpenseToDelete] =
        useState<Expense | null>(null)

    useEffect(() => {
        async function loadApprovals() {
            const approvalEntries =
                await Promise.all(
                    expenses.map(async (expense) => {
                        try {
                            const approval =
                                await employeeApi.getApprovalByExpenseId(
                                    expense.id
                                )

                            return [
                                expense.id,
                                approval,
                            ] as const
                        } catch {
                            return null
                        }
                    })
                )

            const approvalMap: Record<
                number,
                Approval
            > = {}

            for (const entry of approvalEntries) {
                if (entry) {
                    approvalMap[entry[0]] = entry[1]
                }
            }

            setApprovals(approvalMap)
        }

        loadApprovals()
    }, [expenses])

    function handleDelete(
        expense: Expense
    ) {
        const approval =
            approvals[expense.id]

        if (approval?.status !== 'pending') {
            return
        }

        setExpenseToDelete(expense)
    }

    async function confirmDelete() {
        if (!expenseToDelete) {
            return
        }

        try {
            setLoadingId(expenseToDelete.id)

            await employeeApi.deleteExpense(
                expenseToDelete.id
            )

            setExpenseToDelete(null)

            onChanged()
        } catch (error) {
            console.error(error)

            alert(
                'Failed to delete expense.'
            )
        } finally {
            setLoadingId(null)
        }
    }

    async function handleEdit(
        expense: Expense
    ) {
        const approval =
            approvals[expense.id]

        if (approval?.status !== 'pending') {
            return
        }

        onEdit(expense)
    }

    if (expenses.length === 0) {
        return (
            <div className="rounded-2xl border border-slate-200 bg-white p-8 text-center text-slate-500 shadow-sm">
                No expenses found.
            </div>
        )
    }

    return (
        <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
            <div className="overflow-x-auto">
                <table className="w-full text-left text-sm">
                    <thead className="border-b border-slate-200 bg-slate-50">
                        <tr>
                            <th className="px-6 py-4">
                                ID
                            </th>

                            <th className="px-6 py-4">
                                Amount
                            </th>

                            <th className="px-6 py-4">
                                Description
                            </th>

                            <th className="px-6 py-4">
                                Date
                            </th>

                            <th className="px-6 py-4">
                                Status
                            </th>

                            <th className="px-6 py-4">
                                Actions
                            </th>
                        </tr>
                    </thead>

                    <tbody>
                        {expenses.map((expense) => {
                            const approval =
                                approvals[expense.id]

                            const isPending =
                                approval?.status ===
                                'pending'

                            const isLoading =
                                loadingId === expense.id

                            return (
                                <tr
                                    key={expense.id}
                                    className="border-b border-slate-100 last:border-0"
                                >
                                    <td className="px-6 py-4 font-medium text-slate-900">
                                        {expense.id}
                                    </td>

                                    <td className="px-6 py-4 font-semibold text-slate-900">
                                        ${expense.amount}
                                    </td>

                                    <td className="px-6 py-4 text-slate-700">
                                        {expense.description}
                                    </td>

                                    <td className="px-6 py-4 text-slate-700">
                                        {expense.date}
                                    </td>

                                    <td className="px-6 py-4">
                                        <span
                                            className={`rounded-full px-3 py-1 text-xs font-semibold ${
                                                approval?.status ===
                                                'approved'
                                                    ? 'bg-green-100 text-green-700'
                                                    : approval?.status ===
                                                        'denied'
                                                        ? 'bg-red-100 text-red-700'
                                                        : 'bg-yellow-100 text-yellow-700'
                                            }`}
                                        >
                                            {approval?.status
                                                ?.toUpperCase() ??
                                                'LOADING'}
                                        </span>
                                    </td>

                                    <td className="px-6 py-4">
                                        {isPending ? (
                                            <div className="flex gap-2">
                                                <button
                                                    disabled={isLoading}
                                                    onClick={() =>
                                                        handleEdit(
                                                            expense
                                                        )
                                                    }
                                                    className="rounded-lg bg-blue-600 px-3 py-2 text-xs font-semibold text-white hover:bg-blue-700 disabled:opacity-40"
                                                >
                                                    Edit
                                                </button>

                                                <button
                                                    disabled={isLoading}
                                                    onClick={() =>
                                                        handleDelete(
                                                            expense
                                                        )
                                                    }
                                                    className="rounded-lg bg-red-600 px-3 py-2 text-xs font-semibold text-white hover:bg-red-700 disabled:opacity-40"
                                                >
                                                    Delete
                                                </button>
                                            </div>
                                        ) : (
                                        <button
                                            onClick={()=>onReview(expense, approvals[expense.id])}
                                            className="rounded-lg border border-slate-300 px-3 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-40"
                                        >
                                            Review
                                        </button>
                                        )}
                                    </td>
                                </tr>
                            )
                        })}
                    </tbody>
                </table>
            </div>

            {expenseToDelete && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
                    <div className="w-full max-w-md rounded-2xl bg-white p-6 shadow-xl">
                        <h2 className="text-lg font-semibold text-slate-900">
                            Delete Expense
                        </h2>

                        <p className="mt-2 text-sm text-slate-600">
                            Are you sure you want to delete this expense?
                        </p>

                        <div className="mt-4 rounded-lg bg-slate-50 p-4 text-sm">
                            <p>
                                <span className="font-semibold">
                                    Description:
                                </span>{' '}
                                {expenseToDelete.description}
                            </p>

                            <p>
                                <span className="font-semibold">
                                    Amount:
                                </span>{' '}
                                ${expenseToDelete.amount}
                            </p>

                            <p>
                                <span className="font-semibold">
                                    Date:
                                </span>{' '}
                                {expenseToDelete.date}
                            </p>
                        </div>

                        <div className="mt-6 flex justify-end gap-3">
                            <button
                                type="button"
                                onClick={() =>
                                    setExpenseToDelete(null)
                                }
                                disabled={
                                    loadingId ===
                                    expenseToDelete.id
                                }
                                className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-40"
                            >
                                Cancel
                            </button>

                            <button
                                type="button"
                                onClick={confirmDelete}
                                disabled={
                                    loadingId ===
                                    expenseToDelete.id
                                }
                                className="rounded-lg bg-red-600 px-4 py-2 text-sm font-semibold text-white hover:bg-red-700 disabled:opacity-40"
                            >
                                {loadingId ===
                                expenseToDelete.id
                                    ? 'Deleting...'
                                    : 'Delete'}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}