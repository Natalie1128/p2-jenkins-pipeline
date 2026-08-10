import type { Approval, Expense } from "../types/models";

export function downloadCsv(expenses: Expense[], approvalRecord: Record<number, Approval>) {
  const totalSubmittedExpenses = expenses.length;

  const totalSubmittedExpensesCost = expenses.reduce(
    (total, e) => total + Number(e.amount),
    0
  );

  const totalActiveExpenses = expenses.filter(
    (e) => approvalRecord[e.id]?.status.toUpperCase() !== 'DENIED'
  );

  const totalActiveExpensesCost = totalActiveExpenses.reduce(
    (total, e) => total + Number(e.amount),
    0
  );

  const summary = [
    ['Submitted Expenses', totalSubmittedExpenses],
    ['Submitted Cost', `$${totalSubmittedExpensesCost.toFixed(2)}`],
    ['Active Expenses', totalActiveExpenses.length],
    ['Active Cost', `$${totalActiveExpensesCost.toFixed(2)}`],
  ];

  const expenseTable = [
    ['Expense ID', 'Date', 'Amount', 'Status', 'Description'],
    ...expenses.map((e) => [
      e.id,
      e.date,
      Number(e.amount || 0).toFixed(2),
      approvalRecord[e.id]?.status || '',
      e.description || ''
    ])
  ];

  const summaryCsv = summary
    .map((row) => row.map(csvField).join(','))
    .join('\r\n');

  const expenseCsv = expenseTable
    .map((row) => row.map(csvField).join(','))
    .join('\r\n');

  const csv = `${summaryCsv}\r\n\r\n${expenseCsv}`;

  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });

  const url = URL.createObjectURL(blob);

  const link = document.createElement('a');
  link.href = url;
  link.download = generateCsvFilename();
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

function generateCsvFilename(): string {
  const today = new Date()
    .toISOString()
    .split('T')[0]

  return `${today}-summary.csv`
}

function csvField(value: unknown): string {
  const stringValue = String(value ?? '');

  return `"${stringValue.replace(/"/g, '""')}"`;
}