import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import Cookies from 'js-cookie';
import { X } from 'lucide-react';
import { Toaster, toast } from 'react-hot-toast';

const API_URL = import.meta.env.VITE_API_URL;

const JoinGamePage = () => {
    const [gameCode, setGameCode] = useState('');
    const history = useNavigate();

    useEffect(() => {
        Cookies.remove('gameCode');
    }, []);

    const handleSubmit = async (e: React.FormEvent, callback: () => void) => {
        const audio = new Audio('/sounds/unirse.mp3');
        audio.play();
        callback();
        e.preventDefault();

        const codeGame = parseInt(gameCode, 10);

        if (isNaN(codeGame)) {
            toast.error('El código de la partida debe ser un número válido.');
            return;
        }

        try {
            const response = await axios.get(`${API_URL}/Game/Check/${codeGame}`);

            if (response.data) {
                Cookies.set('gameCode', codeGame.toString());
                toast.success('¡Código válido! Entrando a la sala...');
                history('/waiting-room-join');
            } else {
                toast.error('El código de la partida no es válido.');
            }
        } catch (error) {
            console.error('Error al verificar el código de la partida:', error);
            toast.error('Hubo un error al intentar unirse a la partida.');
        }
    };

    const handleExit = () => {
        history('/menu');
    };

    return (
        <div
            className="min-h-screen bg-cover bg-center text-white flex items-center justify-center relative"
            style={{ backgroundImage: "url('/Fichas/Fondo.jpg')" }}
        >
            <Toaster position="top-center" reverseOrder={false} />
            <button
                onClick={handleExit}
                className="absolute top-6 right-6 bg-yellow-300 hover:bg-yellow-400 text-black rounded-full w-10 h-10 flex items-center justify-center shadow-lg transform transition-transform duration-300 hover:scale-110"
            >
                <X size={24} strokeWidth={3} />
            </button>

            <div className="bg-black bg-opacity-60 p-8 rounded-2xl shadow-2xl w-full max-w-md text-center space-y-6">
                <h1 className="text-4xl font-extrabold text-yellow-300 drop-shadow-[3px_3px_0px_#000] tracking-widest uppercase animate-pulse">
                    Unirse a Partida
                </h1>
                <form onSubmit={(e) => handleSubmit(e, () => {})} className="flex flex-col space-y-4">
                    <input
                        type="text"
                        placeholder="Código de la partida"
                        value={gameCode}
                        onChange={(e) => setGameCode(e.target.value)}
                        className="px-4 py-3 rounded-xl bg-white text-gray-800 text-center text-lg font-semibold shadow focus:outline-none focus:ring-2 focus:ring-green-400"
                        required
                    />
                    <button
                        type="submit"
                        onClick={() => new Audio('/sounds/unirse.mp3').play()}
                        className="px-8 py-3 bg-green-500 hover:bg-green-600 text-white text-lg font-bold rounded-full shadow-lg transition-all duration-300"
                    >
                        ¡Unirse!
                    </button>
                </form>
            </div>
        </div>
    );
};

export default JoinGamePage;
