export interface User {
  id: number
  username: string
  password?: string
  role: string
}

export interface Expense {
  id: number
  user_id: number
  amount: number
  description: string
  date: string
}

export interface Approval {
  id?: number
  expense_id: number
  status: string
  reviewer?: number | null
  comment?: string
  review_date?: string
}

export interface Review {
  approval_id?: number
  user_id: number
  expense_id: number
  amount: number
  description: string
  date: string
  status: string
  reviewer?: number | null
  comment?: string
  review_date?: string
}