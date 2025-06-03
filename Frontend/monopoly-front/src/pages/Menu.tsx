import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import {Stats} from '../services/stats'

interface Stats {
    "victories": number,
    "gamesPlayed": number,
    "totalMoney": number,
    "totalProperties": number
}

export default function Menu() {
    const navigate = useNavigate();
    const nickname = Cookies.get('nickname');
    const [showPlayOptions, setShowPlayOptions] = useState(false);
    const [showOptionsModal, setShowOptionsModal] = useState(false);
    const [showStatsModal, setShowStatsModal] = useState(false);
    const [stats, setStats] = useState<Stats[]>([]);
    const [volume, setVolume] = useState(0.05);
    const [isMuted, setIsMuted] = useState(false);

    const audioRef = useRef<HTMLAudioElement | null>(null);

    useEffect(() => {
        Cookies.remove('codeGame');
        if (audioRef.current) {
            audioRef.current.volume = isMuted ? 0 : volume;
            audioRef.current.loop = true;
            audioRef.current.play().catch((e) => console.error('Audio error:', e));
        }
    }, [volume, isMuted]);

    const handleClick = (callback: () => void) => {
        const audio = new Audio('/sounds/click.mp3');
        audio.volume = 0.4;
        audio.play();
        callback();
    };
    const handleStats = async () => {
        try {
            const response = await Stats(nickname);
            setStats(response.data);
            setShowStatsModal(true)
        }catch (error) {
            console.error(error);
        }
    }
    const handleLogout = () => {
        Cookies.remove('nickname');
        Cookies.remove('gameCode');
        navigate('/');
    };

    return (
        <div className="min-h-screen bg-cover bg-center flex items-center justify-center font-mono"
             style={{ backgroundImage: "url('/Fichas/Fondo.jpg')" }}>
            <audio ref={audioRef} src="/sounds/lobby.mp3" autoPlay />
            <div className="p-10 text-white space-y-6 w-full max-w-xl">
                <h2 className="text-4xl font-extrabold text-center text-yellow-300 drop-shadow-md bg-black bg-opacity-50 py-2 rounded-xl">
                    ¡Bienvenido, {nickname}!
                </h2>

                <div className="space-y-4">
                    <button
                        onClick={() => handleClick(() => setShowPlayOptions(!showPlayOptions))}
                        className="w-full bg-yellow-400 hover:bg-yellow-500 py-3 rounded-xl text-black font-bold text-xl transition-transform duration-300 transform hover:scale-105 shadow-md"
                    >
                        🎲 Jugar
                    </button>

                    {showPlayOptions && (
                        <div className="pl-2 space-y-3 animate-fade-in">
                            <button
                                onClick={() => handleClick(() => navigate('/waiting-room-create'))}
                                className="w-full bg-green-500 hover:bg-green-600 py-2 rounded-xl font-semibold transition transform hover:scale-105"
                            >
                                🛠 Crear partida
                            </button>
                            <button
                                onClick={() => handleClick(() => navigate('/page-code'))}
                                className="w-full bg-blue-500 hover:bg-blue-600 py-2 rounded-xl font-semibold transition transform hover:scale-105"
                            >
                                🔗 Unirse a partida
                            </button>
                        </div>
                    )}

                    <button
                        onClick={() => handleClick(() => setShowOptionsModal(true))}
                        className="w-full bg-indigo-400 hover:bg-indigo-500 py-3 rounded-xl font-bold transition-transform transform hover:scale-105"
                    >
                        ⚙️ Opciones
                    </button>

                    <button
                        onClick={() => handleClick(handleStats)
                    }
                        className="w-full bg-purple-400 hover:bg-purple-500 py-3 rounded-xl font-bold transition-transform transform hover:scale-105"
                    >
                        📊 Estadísticas
                    </button>
                    <button
                        onClick={() => handleClick(handleLogout)}
                        className="w-full bg-red-500 hover:bg-red-600 py-3 rounded-xl font-bold transition-transform transform hover:scale-105 mt-4"
                    >
                        🚪 Cerrar sesión
                    </button>
                </div>
            </div>

            {showOptionsModal && (
                <div className="fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-50">
                    <div className="bg-white text-black p-6 rounded-xl shadow-xl w-full max-w-sm space-y-4">
                        <h3 className="text-xl font-bold text-center">🎵 Opciones de sonido</h3>
                        <label className="block">
                            <span className="block font-medium">Volumen</span>
                            <input
                                type="range"
                                min="0"
                                max="1"
                                step="0.01"
                                value={isMuted ? 0 : volume}
                                onChange={(e) => {
                                    setIsMuted(false);
                                    setVolume(parseFloat(e.target.value));
                                }}
                                className="w-full"
                            />
                        </label>
                        <label className="flex items-center gap-2">
                            <input
                                type="checkbox"
                                checked={isMuted}
                                onChange={() => setIsMuted(!isMuted)}
                            />
                            Silenciar música
                        </label>
                        <button
                            onClick={() => setShowOptionsModal(false)}
                            className="w-full bg-yellow-400 hover:bg-yellow-500 py-2 rounded-xl font-bold"
                        >
                            Cerrar
                        </button>
                    </div>
                </div>
            )}
            {showStatsModal && (
                <div className="fixed inset-0 bg-black bg-opacity-70 flex items-center justify-center z-50">
                    <div className="bg-gradient-to-br from-purple-800 to-indigo-700 text-white p-6 rounded-2xl shadow-2xl w-full max-w-md space-y-6 animate-fade-in">
                        <h3 className="text-2xl font-extrabold text-center text-yellow-300 drop-shadow-md">📊 Tus estadísticas</h3>
                        {stats.length > 0 ? (
                            <div className="space-y-4">
                                <div className="bg-black bg-opacity-30 p-4 rounded-xl flex justify-between items-center shadow-inner">
                                    <span className="font-semibold text-lg">🏆 Victorias:</span>
                                    <span className="text-yellow-300 text-xl font-bold">{stats[0].victories}</span>
                                </div>
                                <div className="bg-black bg-opacity-30 p-4 rounded-xl flex justify-between items-center shadow-inner">
                                    <span className="font-semibold text-lg">🎮 Partidas jugadas:</span>
                                    <span className="text-yellow-300 text-xl font-bold">{stats[0].gamesPlayed}</span>
                                </div>
                                <div className="bg-black bg-opacity-30 p-4 rounded-xl flex justify-between items-center shadow-inner">
                                    <span className="font-semibold text-lg">💰 Dinero total ganado:</span>
                                    <span className="text-yellow-300 text-xl font-bold">${stats[0].totalMoney.toLocaleString()}</span>
                                </div>
                                <div className="bg-black bg-opacity-30 p-4 rounded-xl flex justify-between items-center shadow-inner">
                                    <span className="font-semibold text-lg">🏠 Propiedades adquiridas:</span>
                                    <span className="text-yellow-300 text-xl font-bold">{stats[0].totalProperties}</span>
                                </div>
                            </div>
                        ) : (
                            <p className="text-center text-gray-300">No se encontraron estadísticas.</p>
                        )}
                        <button
                            onClick={() => setShowStatsModal(false)}
                            className="w-full bg-yellow-400 hover:bg-yellow-500 py-2 rounded-xl font-bold text-black"
                        >
                            Cerrar
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}
