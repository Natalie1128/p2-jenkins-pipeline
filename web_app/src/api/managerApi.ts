import axios from 'axios';

import type {
  User,
  Expense,
  Approval,
} from '../types/models';

const BASE_URL = 'http://localhost:9090';

export const managerApi = {
  // ====================
  // Authentication
  // ====================

  login: async (
    username: string,
    password: string
  ): Promise<User> => {
    const { data } = await axios.post<User>(
      `${BASE_URL}/users/login`,
      {
        username,
        password,
      }
    );

    return data;
  },

  register: async (
    username: string,
    password: string
  ): Promise<User> => {
    const { data } = await axios.post<User>(
      `${BASE_URL}/users`,
      {
        username,
        password,
        role: "Manager"
      }
    );

    return data;
  },

  // ====================
  // Expenses
  // ====================

  findAllExpenses: async (): Promise<Expense[]> => {
    const { data } = await axios.get<Expense[]>(
      `${BASE_URL}/expenses`
    );

    return data;
  },

  findExpenseById: async (
    expenseId: number
  ): Promise<Expense> => {
    const { data } = await axios.get<Expense>(
      `${BASE_URL}/expenses/${expenseId}`
    );

    return data;
  },

  // ====================
  // Approvals
  // ====================

  findAllApprovals: async (): Promise<Approval[]> => {
    const { data } = await axios.get<Approval[]>(
      `${BASE_URL}/approvals`
    );

    return data;
  },

  getApprovalByExpenseId: async (
    expenseId: number
  ): Promise<Approval | null> => {
    const { data } = await axios.get<Approval[]>(
      `${BASE_URL}/approvals`
    );

    return (
      data.find(
        (approval) =>
          approval.expense_id === expenseId
      ) ?? null
    );
  },

  updateApprovalStatus: async (
    expenseId: number,
    status: string,
    reviewerId: number,
    comment: string
  ): Promise<void> => {
    await axios.put(
      `${BASE_URL}/approvals/${expenseId}`,
      {
        status,
        reviewer: reviewerId,
        comment,
      }
    );
  },
};