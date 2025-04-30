import { useState } from "react";
import { auth } from "../firebase";
import { signInWithEmailAndPassword } from "firebase/auth";
import { useNavigate } from "react-router-dom";
import { Login } from "../services/Auth";
import Cookies from "js-cookie";

export default function LoginPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleLogin = async () => {
        try {
            await signInWithEmailAndPassword(auth, email, password);
            const response = await Login(email, password);
            if (response.success === true) {
                Cookies.set('nickname', response.nickname);
                navigate("/menu");
            } else {
                alert(response.error || "Credenciales incorrectas");
            }
        } catch (error: any) {
            alert(error.message);
        }
    };

    return (
        <div
            className="min-h-screen flex items-center justify-center bg-cover bg-center relative px-4"
            style={{ backgroundImage: "url('/Fichas/Fondo.jpg')" }}
        >
            {/* Capa oscura para contraste */}
            <div className="absolute inset-0 bg-black bg-opacity-70 z-0" />

            <div className="relative z-10 w-full max-w-md text-white text-center">
                <h1 className="text-4xl font-extrabold text-white drop-shadow-md mb-8">
                    ¡Bienvenido a Monopoly!
                </h1>

                <div className="bg-white bg-opacity-10 backdrop-blur-md p-8 rounded-2xl shadow-2xl space-y-6">
                    <input
                        type="email"
                        placeholder="Correo electrónico"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="w-full p-4 rounded-xl text-white bg-black bg-opacity-50 placeholder-gray-300 text-lg focus:outline-none focus:ring-2 focus:ring-yellow-400"
                    />
                    <input
                        type="password"
                        placeholder="Contraseña"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="w-full p-4 rounded-xl text-white bg-black bg-opacity-50 placeholder-gray-300 text-lg focus:outline-none focus:ring-2 focus:ring-yellow-400"
                    />

                    <button
                        onClick={handleLogin}
                        className="w-full py-3 bg-green-500 hover:bg-green-600 text-white text-xl font-bold rounded-full shadow-lg transition"
                    >
                        Iniciar Sesión
                    </button>

                    <button
                        onClick={() => navigate("/register")}
                        className="w-full text-blue-400 hover:text-blue-300 hover:underline text-center mt-4"
                    >
                        ¿No tienes cuenta? Regístrate aquí
                    </button>
                </div>
            </div>
        </div>
    );
}
