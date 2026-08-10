import axios from 'axios';

import type {
  User,
  Expense,
  Approval,
} from '../types/models';

const BASE_URL = 'http://localhost:8080';

export const employeeApi = {
  register: async (
    username: string,
    password: string,
  ): Promise<User> => {
    const { data } = await axios.post<User>(
      `${BASE_URL}/users`,
      { username, password, role: "Employee" }
    );

    return data;
  },

  login: async (
    username: string,
    password: string
  ): Promise<User> => {
    const { data } = await axios.post<User>(
      `${BASE_URL}/users/login`,
      { username, password }
    );

    return data;
  },

  getExpenseById: async (
    expenseId: number
  ): Promise<Expense> => {
    const { data } = await axios.get<Expense>(
      `${BASE_URL}/expenses/${expenseId}`
    );

    return data;
  },

  getMyExpenses: async (
    userId: number
  ): Promise<Expense[]> => {
    const { data } = await axios.get<Expense[]>(
      `${BASE_URL}/expenses/user/${userId}`
    );

    return data;
  },

  getMyExpenseHistory: async (
    userId: number
  ): Promise<Expense[]> => {
    const { data } = await axios.get<Expense[]>(
      `${BASE_URL}/expenses/user/${userId}/history`
    );

    return data;
  },

  createExpense: async (
    expense: Omit<Expense, 'id'>
  ): Promise<Expense> => {
    const { data } = await axios.post<Expense>(
      `${BASE_URL}/expenses`,
      expense
    );

    return data;
  },

  updateExpense: async (
    expenseId: number,
    expense: Omit<Expense, 'id'>
  ): Promise<void> => {
    await axios.put(
      `${BASE_URL}/expenses/${expenseId}`,
      expense
    );
  },

  deleteExpense: async (
    expenseId: number
  ): Promise<void> => {
    await axios.delete(
      `${BASE_URL}/expenses/${expenseId}`
    );
  },

  getApprovalByExpenseId: async (
    expenseId: number
  ): Promise<Approval> => {
    const { data } = await axios.get<Approval>(
      `${BASE_URL}/approvals/expense/${expenseId}`
    );

    return data;
  },

  createApproval: async (
    approval: Omit<Approval, 'id'>
  ): Promise<Approval> => {
    const { data } = await axios.post<Approval>(
      `${BASE_URL}/approvals`,
      approval
    );

    return data;
  },
};