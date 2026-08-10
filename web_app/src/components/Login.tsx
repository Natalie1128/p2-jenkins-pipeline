import { useEffect, useState } from 'react'
import { employeeApi } from '../api/employeeApi'
import type { User } from '../types/models'
import type { SubmitEvent } from 'react'
import { ViewIcon, ViewOffIcon } from './Icons'
import { useNavigate } from 'react-router-dom'

export default function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [isPassHidden, setIsPassHidden] = useState(true)

  const navigate = useNavigate()
  
  useEffect(() => {
    const savedUser = localStorage.getItem('user')

    if (!savedUser) {
      return
    }

    const user: User = JSON.parse(savedUser)

    if (user.role === 'Manager') {
      navigate('/manager', { replace: true })
    } else {
      navigate('/employee', { replace: true })
    }
  }, [navigate])

  async function handleSubmit(
    event: SubmitEvent<HTMLElement>
  ) {
    event.preventDefault()

    setError('')
    setLoading(true)

    try {
      const user = await employeeApi.login(
        username,
        password
      )

      handleLogin(user)
    } catch {
      setError(
        'Username or password not valid.'
      )
    } finally {
      setLoading(false)
    }
  }

  function handleLogin(user: User) {
    localStorage.setItem('user', JSON.stringify(user))

    if (user.role === 'Manager') {
      navigate('/manager')
    } else {
      navigate('/employee')
    }
  }

  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center px-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold tracking-tight text-slate-900">
            Revature Expense App
          </h1>

          <p className="mt-2 text-sm text-slate-500">
            Manage and track your expenses
          </p>
        </div>

        {/* Login Card */}
        <form
          className="rounded-2xl border border-slate-200 bg-white p-8 shadow-xl shadow-slate-200/50"
          onSubmit={handleSubmit}
        >
          <div className="mb-6">
            <h2 className="text-xl font-semibold text-slate-900">
              Welcome back
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
                    type={isPassHidden ? "password" : "text"}
                    placeholder="Enter your password"
                    value={password}
                    onChange={(event) =>
                    setPassword(event.target.value)
                    }
                    className="w-full rounded-lg border border-slate-300 bg-white px-4 py-3 text-sm text-slate-900 outline-none transition placeholder:text-slate-400 focus:border-blue-500 focus:ring-4 focus:ring-blue-500/10"
                />
                <button className="absolute right-3 top-1/2 -translate-y-1/2 hover:cursor-pointer" type="button" onClick={()=>setIsPassHidden(!isPassHidden)}>
                    {isPassHidden ? <ViewOffIcon /> : <ViewIcon />}
                </button>
              </div>
            </div>

            {/* Error */}
            {error && (
              <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3">
                <p className="text-sm text-red-600">
                  {error}
                </p>
              </div>
            )}

            {/* Submit */}
            <button
              type="submit"
              disabled={loading}
              className="w-full rounded-lg bg-blue-600 px-4 py-3 text-sm font-semibold text-white shadow-sm transition hover:bg-blue-700 focus:outline-none focus:ring-4 focus:ring-blue-500/20 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading ? 'Logging in...' : 'Login'}
            </button>
            <div className="text-slate-400 w-full flex justify-center"><a onClick={()=>navigate("/register")} className="hover:text-blue-400 hover:cursor-default">Or register</a></div>
          </div>
        </form>
      </div>
    </div>
  )
}