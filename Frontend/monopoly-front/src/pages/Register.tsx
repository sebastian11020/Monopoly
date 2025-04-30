import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {auth} from '../firebase.ts'
import {Register} from '../services/Auth.ts'
import { createUserWithEmailAndPassword } from "firebase/auth";

export default function RegisterPage() {
    const [email, setEmail] = useState('');
    const [nickname, setNickname] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleRegister = async () => {
        try {
            await createUserWithEmailAndPassword(auth, email, password);
            const response = await Register(email,nickname,password);
            if (response.data.success){
                //aqui va alerta
                navigate("/");
            }else {
                //aqui va alerta
                console.error(response.data.error)
            }
        } catch (error: any) {
            alert(error.message);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-cover bg-center text-white" style={{ backgroundImage: "url('/Fichas/Fondo.jpg')" }}>
            <div className="bg-black bg-opacity-70 p-10 rounded-xl w-full max-w-md text-center space-y-6 shadow-xl">
                <h1 className="text-3xl font-bold">Registro</h1>
                <input
                    type="email"
                    placeholder="Correo electrónico"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full p-3 rounded-md text-black"
                />
                <input
                    type="text"
                    placeholder="Nickname"
                    value={nickname}
                    onChange={(e) => setNickname(e.target.value)}
                    className="w-full p-3 rounded-md text-black"
                />
                <input
                    type="password"
                    placeholder="Contraseña"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full p-3 rounded-md text-black"
                />
                <button
                    onClick={handleRegister}
                    className="w-full bg-green-500 hover:bg-green-600 py-2 rounded-xl font-bold transition-all"
                >
                    Registrarse
                </button>

                <button
                    onClick={() => navigate('/')}
                    className="text-yellow-300 underline mt-2"
                >
                    Volver al inicio de sesión
                </button>
            </div>
        </div>
    );
}
