import { createContext, useContext, useEffect, useState } from "react";
import type { User } from "../types/models";

type AuthContextType = {
    user: User | null;
    logout: () => void;
    loading: boolean;
};

const AuthContext = createContext<AuthContextType | null>(null);


export function AuthProvider({ children }: { children: React.ReactNode }) {

    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);


    useEffect(() => {
        const savedUser = localStorage.getItem("user");

        if (savedUser) {
            setUser(JSON.parse(savedUser));
        }

        setLoading(false);
    }, []);


    useEffect(() => {

        const handleStorageChange = (event: StorageEvent) => {

            if (event.key === "user" && event.newValue === null) {
                setUser(null);
                window.location.href = "/login";
            }

        };


        window.addEventListener(
            "storage",
            handleStorageChange
        );


        return () => {
            window.removeEventListener(
                "storage",
                handleStorageChange
            );
        };

    }, []);


    function logout() {
        localStorage.removeItem("user");
        setUser(null);
    }


    return (
        <AuthContext.Provider value={{ user, logout, loading }}>
            {children}
        </AuthContext.Provider>
    );
}


export function useAuth() {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error(
            "useAuth must be used inside AuthProvider"
        );
    }

    return context;
}