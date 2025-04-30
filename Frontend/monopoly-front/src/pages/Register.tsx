import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { auth } from '../firebase';
import { Register } from '../services/Auth';
import { createUserWithEmailAndPassword } from 'firebase/auth';

export default function RegisterPage() {
    const [email, setEmail] = useState('');
    const [nickname, setNickname] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleRegister = async () => {
        try {
            await createUserWithEmailAndPassword(auth, email, password);
            const response = await Register(email, nickname, password);
            if (response.success === true) {
                alert("¡Registro exitoso!");
                navigate("/");
            } else {
                alert(response.error || "Error al registrar.");
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
            {/* Capa oscura detrás del contenido */}
            <div className="absolute inset-0 bg-black bg-opacity-70 z-0" />

            <div className="relative z-10 w-full max-w-md text-white">
                <h1 className="text-4xl font-extrabold text-center mb-8 text-white drop-shadow-md">
                    ¡Regístrate para jugar!
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
                        type="text"
                        placeholder="Nickname"
                        value={nickname}
                        onChange={(e) => setNickname(e.target.value)}
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
                        onClick={handleRegister}
                        className="w-full py-3 bg-green-500 hover:bg-green-600 text-white text-xl font-bold rounded-full shadow-lg transition"
                    >
                        Registrarse
                    </button>

                    <button
                        onClick={() => navigate('/')}
                        className="w-full text-blue-400 hover:text-blue-300 hover:underline text-center mt-4"
                    >
                        Volver al inicio de sesión
                    </button>
                </div>
            </div>
        </div>
    );
}
