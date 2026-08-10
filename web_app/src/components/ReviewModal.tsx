import type { Review } from "../types/models"

interface ReviewModalProps {
  onClose: () => void
  review: Review
  isManager: boolean
}

export const ReviewModal = ({
    onClose,
    review,
    isManager
}: ReviewModalProps) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4" onClick={onClose}>
      <div className="w-full max-w-lg rounded-2xl bg-white shadow-xl" onClick={(e)=>e.stopPropagation()}>
        {/* Header */}
        <div className="flex items-center justify-between border-b border-slate-200 px-6 py-5">
          <div>
            <h2 className="text-xl font-bold text-slate-900">
              Review Expense
            </h2>

            <p className="mt-1 text-sm text-slate-500">
              Expense #{review.expense_id}
            </p>
          </div>
        </div>

        {/* Expense Details */}
        <div className="space-y-5 px-6 py-6">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <p className="text-sm text-slate-500">
                {isManager ? "Employee ID" : "Manager ID"}
              </p>

              <p className="mt-1 font-semibold text-slate-900">
                {isManager ? String(review.user_id) : String(review.reviewer) }
              </p>
            </div>

            <div>
              <p className="text-sm text-slate-500">
                Review Date
              </p>

              <p className="mt-1 font-semibold text-slate-900">
                {review.review_date}
              </p>
            </div>
          </div>

          <div>
            <p className="text-sm text-slate-500">
              Amount
            </p>

            <p className="mt-1 text-3xl font-bold text-slate-900">
              ${review.amount.toFixed(2)}
            </p>
          </div>

          <div>
            <p className="text-sm text-slate-500">
              Description
            </p>

            <div className="mt-1 rounded-lg bg-slate-50 p-4 text-slate-700">
              {review.description}
            </div>
          </div>

          <div>
            <p className="text-sm text-slate-500">
              Current Status
            </p>

            <span
              className={`mt-2 inline-block rounded-full px-3 py-1 text-sm font-semibold ${
                    review.status.toUpperCase() === "APPROVED"
                    ? "bg-green-100 text-green-800"
                    : "bg-red-100 text-red-800"
              }`}
            >
              {review.status.toUpperCase()}
            </span>
          </div>

          {review.comment && (
            <div>
              <p className="text-sm text-slate-500">
                Review Comment
              </p>

              <div className="mt-1 rounded-lg bg-slate-50 p-4 text-slate-700">
                {review.comment}
              </div>
            </div>
          )}
        </div>

        {/* Actions */}
        <div className="flex justify-end gap-3 border-t border-slate-200 px-6 py-4">
          <button
            type="button"
            onClick={onClose}
            className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50"
          >
            Close
          </button>

          {review.status === "PENDING" && (
            <>
              <button
                type="button"
                className="rounded-lg bg-red-600 px-4 py-2 text-sm font-semibold text-white hover:bg-red-700"
              >
                Reject
              </button>

              <button
                type="button"
                className="rounded-lg bg-green-600 px-4 py-2 text-sm font-semibold text-white hover:bg-green-700"
              >
                Approve
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  )
}