import { useState } from 'react'
import { employeeApi } from '../api/employeeApi'
import type { User } from '../types/models'

interface SubmitExpenseProps {
  user: User
  onCreated: () => void
  onCancel: () => void
}

export default function SubmitExpense({
  user,
  onCreated,
  onCancel,
}: SubmitExpenseProps) {
  const [amount, setAmount] =
    useState('')

  const [description, setDescription] =
    useState('')

  const [date, setDate] =
    useState('')

  const [error, setError] =
    useState('')

  const [loading, setLoading] =
    useState(false)

  async function handleSubmit(
    event: React.SubmitEvent
  ) {
    event.preventDefault()

    setError('')
    setLoading(true)

    try {
      const expense =
        await employeeApi.createExpense({
          user_id: user.id,
          amount: Number(amount),
          description,
          date,
        })

      await employeeApi.createApproval({
        expense_id: expense.id,
        status: 'pending',
        reviewer: null,
        comment: '',
        review_date: '',
      })

      onCreated()
    } catch (error) {
      console.error(error)

      setError(
        'Failed to submit expense.'
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="mx-auto max-w-2xl">
      <div className="mb-6">
        <h2 className="text-3xl font-bold text-slate-900">
          Submit Expense
        </h2>

        <p className="mt-2 text-slate-500">
          Submit a new expense report for approval.
        </p>
      </div>

      <form
        onSubmit={handleSubmit}
        className="rounded-2xl border border-slate-200 bg-white p-8 shadow-sm"
      >
        <div className="space-y-5">
          {/* Amount */}
          <div>
            <label
              htmlFor="amount"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Amount
            </label>

            <input
              id="amount"
              type="number"
              min="0.01"
              step="0.01"
              value={amount}
              onChange={(event) =>
                setAmount(
                  event.target.value
                )
              }
              placeholder="Enter expense amount"
              required
              className="w-full rounded-lg border border-slate-300 px-4 py-3 text-sm outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10"
            />
          </div>

          {/* Description */}
          <div>
            <label
              htmlFor="description"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Description
            </label>

            <textarea
              id="description"
              value={description}
              onChange={(event) =>
                setDescription(
                  event.target.value
                )
              }
              placeholder="Describe the expense"
              rows={4}
              required
              className="w-full resize-none rounded-lg border border-slate-300 px-4 py-3 text-sm outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10"
            />
          </div>

          {/* Date */}
          <div>
            <label
              htmlFor="date"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Expense Date
            </label>

            <input
              id="date"
              type="date"
              value={date}
              max={
                new Date()
                  .toISOString()
                  .split('T')[0]
              }
              onChange={(event) =>
                setDate(
                  event.target.value
                )
              }
              required
              className="w-full rounded-lg border border-slate-300 px-4 py-3 text-sm outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10"
            />
          </div>

          {/* Error */}
          {error && (
            <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3">
              <p className="text-sm text-red-600">
                {error}
              </p>
            </div>
          )}

          {/* Buttons */}
          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={onCancel}
              disabled={loading}
              className="rounded-lg border border-slate-300 px-4 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-40"
            >
              Cancel
            </button>

            <button
              type="submit"
              disabled={loading}
              className="rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading
                ? 'Submitting...'
                : 'Submit Expense'}
            </button>
          </div>
        </div>
      </form>
    </div>
  )
}