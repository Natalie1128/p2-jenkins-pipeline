import { useState } from "react"
import type { Approval } from "../types/models"

type Decision = "APPROVED" | "DENIED"

interface DecisionModalProps {
  onClose: () => void
  onSubmit: (
    expenseId: number, status: string, reviewId: number, comment: string
  ) => void,
  approval: Approval
  reviewerID: number
}

export const DecisionModal = ({
  onClose,
  onSubmit,
  approval,
  reviewerID
}: DecisionModalProps) => {
  const [comment, setComment] = useState("")

  function handleSubmit(approval: Approval, decision: Decision, description: string) {
    if(approval && approval.id){
        onSubmit(approval.expense_id, decision.toLowerCase(), reviewerID, description)
    }
  }

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
      onClick={onClose}
    >
      <div
        className="w-full max-w-lg rounded-2xl bg-white shadow-xl"
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between border-b border-slate-200 px-6 py-5">
          <div>
            <h2 className="text-xl font-bold text-slate-900">
              Review Expense
            </h2>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="rounded-lg p-2 text-slate-400 hover:bg-slate-100 hover:text-slate-600"
            aria-label="Close modal"
          >
            ✕
          </button>
        </div>

        {/* Comment */}
        <div className="px-6 py-6">
          <label
            htmlFor="decision-comment"
            className="text-sm font-semibold text-slate-700"
          >
            Comment
          </label>

          <textarea
            id="decision-comment"
            value={comment}
            onChange={(e) => setComment(e.target.value)}
            placeholder="Enter comment here..."
            rows={5}
            className="mt-2 w-full resize-none rounded-lg border border-slate-300 px-4 py-3 text-sm text-slate-900 outline-none placeholder:text-slate-400 focus:border-slate-500 focus:ring-2 focus:ring-slate-200"
          />
        </div>

        {/* Actions */}
        <div className="flex justify-end gap-3 border-t border-slate-200 px-6 py-4">
          <button
            type="button"
            onClick={() => handleSubmit(approval, "APPROVED", comment)}
            className="rounded-lg bg-green-600 px-4 py-2 text-sm font-semibold text-white hover:bg-green-700"
          >
            Approve
          </button>
          <button
            type="button"
            onClick={() => handleSubmit(approval, "DENIED", comment)}
            className="rounded-lg bg-red-600 px-4 py-2 text-sm font-semibold text-white hover:bg-red-700"
          >
            Deny
          </button>
        </div>
      </div>
    </div>
  )
}