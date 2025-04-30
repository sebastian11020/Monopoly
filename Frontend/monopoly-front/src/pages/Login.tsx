import { useState } from "react";
import { auth } from "../firebase";
import { signInWithEmailAndPassword } from "firebase/auth";
import { useNavigate } from "react-router-dom";
import {Login} from '../services/Auth.ts'

export default function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleLogin = async () => {
        try {
            await signInWithEmailAndPassword(auth, email, password);
            const response = await Login(email,password);
            if (response.data.success){
                navigate("/menu");
            }else{
                //aqui va alerta
                console.error(response.data.error)
            }
        } catch (error: any) {
            alert(error.message);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white">
            <div className="bg-gray-800 p-8 rounded shadow-lg w-full max-w-md space-y-4">
                <h1 className="text-2xl font-bold mb-4">Iniciar Sesión</h1>
                <input
                    type="email"
                    placeholder="Correo"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full p-2 rounded text-black"
                />
                <input
                    type="password"
                    placeholder="Contraseña"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full p-2 rounded text-black"
                />
                <button onClick={handleLogin} className="w-full bg-blue-500 py-2 rounded hover:bg-blue-600">
                    Iniciar Sesión
                </button>
                <button
                    onClick={() => navigate("/register")}
                    className="w-full bg-gray-600 py-2 rounded hover:bg-gray-700"
                >
                    Registrarse
                </button>
            </div>
        </div>
    );
}
