import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';

export default function Menu() {
    const navigate = useNavigate();
    const nickname = Cookies.get('nickname');
    const [showPlayOptions, setShowPlayOptions] = useState(false);

    const handleLogout = () => {
        Cookies.remove('nickname');
        Cookies.remove('gameCode');
        navigate('/');
    };

    return (
        <div className="min-h-screen bg-cover bg-center flex items-center justify-center" style={{ backgroundImage: "url('/Fichas/Fondo.jpg')" }}>
            <div className="bg-black bg-opacity-70 p-10 rounded-2xl text-white space-y-6 w-full max-w-md shadow-2xl">
                <h2 className="text-2xl font-bold text-center">Hola, {nickname}</h2>

                <div className="space-y-4">
                    {/* Botón principal "Jugar" */}
                    <button
                        onClick={() => setShowPlayOptions(!showPlayOptions)}
                        className="w-full bg-yellow-400 hover:bg-yellow-500 py-2 rounded-xl font-bold transition-all"
                    >
                        Jugar
                    </button>

                    {showPlayOptions && (
                        <div className="pl-4 space-y-2 transition-all duration-300">
                            <button
                                onClick={() => navigate('/waiting-room-create')}
                                className="w-full bg-green-500 hover:bg-green-600 py-2 rounded-lg font-semibold"
                            >
                                Crear partida
                            </button>
                            <button
                                onClick={() => navigate('/page-code')}
                                className="w-full bg-blue-500 hover:bg-blue-600 py-2 rounded-lg font-semibold"
                            >
                                Unirse a partida
                            </button>
                        </div>
                    )}

                    <button
                        onClick={() => navigate('/opciones')}
                        className="w-full bg-blue-400 hover:bg-blue-500 py-2 rounded-xl font-bold"
                    >
                        Opciones
                    </button>

                    <button
                        onClick={() => navigate('/estadisticas')}
                        className="w-full bg-purple-400 hover:bg-purple-500 py-2 rounded-xl font-bold"
                    >
                        Estadísticas
                    </button>

                    <button
                        onClick={() => navigate('/historial')}
                        className="w-full bg-pink-400 hover:bg-pink-500 py-2 rounded-xl font-bold"
                    >
                        Historial de Partidas
                    </button>

                    <button
                        onClick={handleLogout}
                        className="w-full bg-red-500 hover:bg-red-600 py-2 rounded-xl font-bold mt-4"
                    >
                        Cerrar sesión
                    </button>
                </div>
            </div>
        </div>
    );
}
