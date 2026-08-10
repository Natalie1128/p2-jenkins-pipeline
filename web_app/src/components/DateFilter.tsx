type DateFilterProps = {
  day: string
  month: string
  year: string
  setDay: (day: string) => void
  setMonth: (month: string) => void
  setYear: (year: string) => void
}

export function DateFilter({
  day,
  month,
  year,
  setDay,
  setMonth,
  setYear,
}: DateFilterProps) {
  const currentYear = new Date().getFullYear()

  const daysInMonth =
    month && year
      ? new Date(Number(year), Number(month), 0).getDate()
      : 31

  return (
    <div className="flex items-center gap-2">
      <label className="text-sm font-semibold text-slate-700">
        Date
      </label>

      {/* Month */}
      <select
        value={month}
        onChange={(e) => setMonth(e.target.value)}
        className="rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-700"
      >
        <option value="">All Months</option>

        {Array.from({ length: 12 }, (_, index) => {
          const monthNumber = String(index + 1).padStart(2, '0')

          return (
            <option key={monthNumber} value={monthNumber}>
              {new Date(2000, index).toLocaleString('default', {
                month: 'long',
              })}
            </option>
          )
        })}
      </select>

      {/* Day */}
        {month &&
            <select
                value={day}
                onChange={(e) => setDay(e.target.value)}
                className="rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-700"
            >
                <option value="">All Days</option>

                {Array.from({ length: daysInMonth }, (_, index) => {
                const dayNumber = String(index + 1).padStart(2, '0')

                return (
                    <option key={dayNumber} value={dayNumber}>
                    {index + 1}
                    </option>
                )
                })}
            </select>
        }

      {/* Year */}
      <select
        value={year}
        onChange={(e) => setYear(e.target.value)}
        className="rounded-lg border border-slate-300 px-3 py-2 text-sm text-slate-700"
      >
        <option value="">All Years</option>

        {Array.from(
          { length: currentYear - 2024 + 1 },
          (_, index) => currentYear - index
        ).map((yearOption) => (
          <option key={yearOption} value={yearOption}>
            {yearOption}
          </option>
        ))}
      </select>
    </div>
  )
}