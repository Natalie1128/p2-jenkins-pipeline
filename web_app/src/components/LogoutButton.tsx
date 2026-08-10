import { useNavigate } from 'react-router-dom'

export const LogoutButton = () => {
    const navigate = useNavigate()

    const handleLogout = ()=>{
        localStorage.removeItem('user')
        navigate('/')
    }
    return (
        <button
        onClick={handleLogout}
        className="rounded-lg border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50"
        >
        Logout
        </button>
    )
}