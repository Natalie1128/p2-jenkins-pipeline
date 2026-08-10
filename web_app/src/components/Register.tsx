import { useState } from 'react'
import { employeeApi } from '../api/employeeApi'
import type { SubmitEvent } from 'react'
import { ViewIcon, ViewOffIcon } from './Icons'
import { useNavigate } from 'react-router-dom'
import { managerApi } from '../api/managerApi'

interface RegisterProps {
}

type Role = 'EMPLOYEE' | 'MANAGER'

export default function Register({
}: RegisterProps) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [role, setRole] = useState<Role>('EMPLOYEE')
  const [error, setError] = useState('')
  const [isRegisterSuccessfully, setIsRegisterSuccessfully] = useState(false)
  const [loading, setLoading] = useState(false)
  const [isPassHidden, setIsPassHidden] = useState(true)

  const navigate = useNavigate()

  const handleSuccessfulRegister = () => {
    setError("")
    setIsRegisterSuccessfully(true)
    setUsername("")
    setPassword("")
  }

  async function handleSubmit(
    event: SubmitEvent<HTMLFormElement>
  ) {
    event.preventDefault()

    setError('')
    setLoading(true)

    try {
        if(role === "EMPLOYEE"){
            await employeeApi.register(
                username,
                password
            )
        }else{
            await managerApi.register(
                username,
                password
            )
        }

        handleSuccessfulRegister()
        
        } catch {
        setError(
            'Unable to create account.'
        )
        } finally {
        setLoading(false)
        }
  }

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center px-4">
      <div className="w-full max-w-md">
        <div className="mb-8 text-center">
          <h1 className="text-3xl font-bold tracking-tight text-slate-900">
            Revature Expense App
          </h1>

          <p className="mt-2 text-sm text-slate-500">
            Manage and track your expenses
          </p>
        </div>

        <form
          className="rounded-2xl border border-slate-200 bg-white p-8 shadow-xl shadow-slate-200/50"
          onSubmit={handleSubmit}
        >
          <div className="mb-6">
            <h2 className="text-xl font-semibold text-slate-900">
              Create an account
            </h2>
          </div>

          <div className="space-y-5">
            {/* Username */}
            <div>
              <label
                htmlFor="username"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Username
              </label>

              <input
                id="username"
                type="text"
                placeholder="Enter your username"
                value={username}
                onChange={(event) =>
                  setUsername(event.target.value)
                }
                className="w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10"
              />
            </div>

            {/* Password */}
            <div>
              <label
                htmlFor="password"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Password
              </label>

              <div className="relative">
                <input
                  id="password"
                  type={
                    isPassHidden
                      ? 'password'
                      : 'text'
                  }
                  placeholder="Enter your password"
                  value={password}
                  onChange={(event) =>
                    setPassword(event.target.value)
                  }
                  className="w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10"
                />

                <button
                  className="absolute right-3 top-1/2 -translate-y-1/2 hover:cursor-pointer"
                  type="button"
                  onClick={() =>
                    setIsPassHidden(
                      !isPassHidden
                    )
                  }
                >
                  {isPassHidden ? (
                    <ViewOffIcon />
                  ) : (
                    <ViewIcon />
                  )}
                </button>
              </div>
            </div>

            {/* Role */}
            <div>
              <label
                htmlFor="role"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Role
              </label>

              <select
                id="role"
                value={role}
                onChange={(event) =>
                  setRole(
                    event.target.value as Role
                  )
                }
                className="w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 outline-none focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10"
              >
                <option value="EMPLOYEE">
                  Employee
                </option>

                <option value="MANAGER">
                  Manager
                </option>
              </select>
            </div>

            {/* Error */}
            {error && (
              <div id="error-msg" className="rounded-lg border border-red-200 bg-red-50 px-4 py-3">
                <p className="text-sm text-red-600">
                  {error}
                </p>
              </div>
            )}
            {isRegisterSuccessfully && (
                <div id="success-msg" className="rounded-lg border border-green-200 bg-green-50 px-4 py-3">
                    <p className="text-sm text-green-600">
                    Register succesfully!
                    </p>
                </div>
                )
            }

            {/* Submit */}
            <button
              type="submit"
              disabled={loading}
              className="w-full rounded-lg bg-blue-600 px-4 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-700 focus:outline-none focus:ring-4 focus:ring-blue-500/20 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading
                ? 'Creating account...'
                : 'Register'}
            </button>
            <div className="text-slate-400 w-full flex justify-center"><a onClick={()=>navigate("/login")} className="hover:text-blue-400 hover:cursor-default">Have an account? Log in</a></div>
          </div>
        </form>
      </div>
    </div>
  )
}