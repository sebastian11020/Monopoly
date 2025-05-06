import { useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';
import { X } from 'lucide-react';
import Cookies from 'js-cookie';
import Header from '../components/header';
import GameCode from '../components/gameCode';
import axios from 'axios'
import PlayerList from '../components/playerList';
import TokenSelector from '../components/TokenSelector';
import { useNavigate } from 'react-router-dom';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

export default function WaitingRoom() {
    const [players, setPlayers] = useState<any[]>([]);
    const [roomCode, setRoomCode] = useState('');
    const [isConnected, setIsConnected] = useState(false);
    const [showSettings, setShowSettings] = useState<boolean>(false);
    const [showReconnectModal, setShowReconnectModal] = useState(false);
    const [pendingCode, setPendingCode] = useState('');
    const [volume, setVolume] = useState(0.05);
    const [isMuted, setIsMuted] = useState(false);
    const audioRef = useRef<HTMLAudioElement | null>(null);
    const client = useRef<Client | null>(null);
    const history = useNavigate();
    const nickname = Cookies.get('nickname');

    useEffect(() => {
        const createGameAndConnectWS = async () => {
            try {
                const response = await axios.get('http://localhost:8003/api/game/create');
                const data = response.data;
                console.log('Datos de CreateGame:', data);

                if (data.success) {
                    setRoomCode(data.codeGame);
                    Cookies.set('gameCode', data.codeGame);
                    toast.success(`Sala creada: ${data.codeGame}`);

                    if (data.gamePlayers) {
                        setPlayers(data.gamePlayers.map((p: any) => ({
                            nickname: p.nickName,
                            token: p.namePiece || '',
                            state: p.state,
                        })));
                    }
                    const stompClient = new Client({
                        brokerURL: 'ws://localhost:8003/app',
                        reconnectDelay: 1000,
                        onConnect: () => {
                            console.log('Conectado al WebSocket');
                            setIsConnected(true);
                        },
                        onStompError: (frame) => {
                            console.error('Error STOMP:', frame);
                            toast.error('Error al conectar con la sala de espera');
                        }
                    });

                    stompClient.activate();
                    client.current = stompClient;

                } else if (data.error?.includes('El jugador ya encuentra registrado')) {
                    setPendingCode(data.codeGame);
                    setShowReconnectModal(true);
                }
            } catch (error) {
                console.error('Error al crear la sala:', error);
                toast.error('No se pudo crear la sala de juego');
            }
        };
        createGameAndConnectWS();
        return () => {
            if (client.current?.active) {
                client.current.deactivate();
            }
        };
    }, []);

    useEffect(() => {
        if (!roomCode || !client.current?.connected) return;
        const stompClient = client.current;
        const updatePlayers = (data: any) => {
            setPlayers(data.gamePlayers.map((p: any) => ({
                nickname: p.nickName,
                token: p.namePiece || '',
                state: p.state,
            })));
        };

        stompClient.subscribe(`/topic/JoinGame/${roomCode}`, (message) => {
            const data = JSON.parse(message.body);
            console.log('JoinGame:', data);
            if (data.success) updatePlayers(data);
        });

        stompClient.subscribe(`/topic/SelectPieceGame/${roomCode}`, (message) => {
            const data = JSON.parse(message.body);
            if (data.success) {
                const updatedPlayer = data.gamePlayer;
                setPlayers(prev =>
                    prev.map(p =>
                        p.nickname === updatedPlayer.nickName
                            ? { ...p, token: updatedPlayer.namePiece }
                            : p
                    )
                );
            }
        });

        stompClient.subscribe(`/topic/ChangeStatePlayer/${roomCode}`, (message) => {
            const data = JSON.parse(message.body);
            if (data.success && data.gamePlayers) {
                updatePlayers(data);
            }
        });

        stompClient.subscribe(`/topic/Exit/${roomCode}`, (message) => {
            const data = JSON.parse(message.body);
            if (data.success) {
                updatePlayers(data);
                toast.warning('Un jugador ha salido de la sala');
            }
        });

        if (nickname) {
            const gamePlayer = {
                idGame: parseInt(roomCode),
                nickName: nickname,
            };
            stompClient.publish({
                destination: '/Game/JoinGame',
                body: JSON.stringify(gamePlayer),
            });
        }
    }, [roomCode, isConnected]);

    useEffect(() => {
        if (audioRef.current) {
            audioRef.current.volume = isMuted ? 0 : volume;
            audioRef.current.loop = true;
            audioRef.current.play().catch((e) => console.error('Audio error:', e));
        }
    }, [volume, isMuted]);

    const handleExit = () => {
        const gameCode = Cookies.get('gameCode');
        const nickName = Cookies.get('nickname');

        if (nickName && gameCode && client.current?.connected) {
            const exitGame = {
                nickName,
                codeGame: parseInt(gameCode),
            };
            client.current.publish({
                destination: '/Game/Exit',
                body: JSON.stringify(exitGame),
            });
        }

        Cookies.remove('gameCode');
        toast.info('Has salido de la sala');
        history('/menu');
    };

    const handleStartGame = () => {
        console.log('Empezando partida...');
        toast.success('¡La partida está comenzando!');
    }

    const handleReconnect = () => {
        Cookies.set('gameCode', String(pendingCode));
        setRoomCode(String(pendingCode));
        setShowReconnectModal(false);
    };

    const handleNewGame = () => {
        if (nickname && client.current?.connected) {
            const exitGame = {
                nickName: nickname,
                codeGame: parseInt(pendingCode),
            };
            client.current.publish({
                destination: '/Game/Exit',
                body: JSON.stringify(exitGame),
            });

            Cookies.remove('gameCode');
            client.current.publish({
                destination: '/Game/Create',
                body: nickname,
            });
        }

        setShowReconnectModal(false);
    };

    const allReady = players.length > 1 && players
        .filter(p => p.nickname !== nickname)
        .every(p => p.state);

    return (
        <div className="min-h-screen bg-cover bg-center text-white" style={{ backgroundImage: "url('/Fichas/Fondo.jpg')" }}>
            <button
                onClick={handleExit}
                className="absolute top-6 right-6 bg-yellow-300 hover:bg-yellow-400 text-black rounded-full w-10 h-10 flex items-center justify-center shadow-lg transform transition-transform duration-300 hover:scale-110"
            >
                <X size={24} strokeWidth={3} />
            </button>
            <audio ref={audioRef} src="/sounds/waiting-room.mp3" autoPlay />
            <button
                onClick={() => setShowSettings(true)}
                className="absolute top-6 right-20 bg-yellow-300 hover:bg-yellow-400 text-black rounded-full w-10 h-10 flex items-center justify-center shadow-lg transform transition-transform duration-300 hover:scale-110"
            >
                <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42M9 12a3 3 0 1 0 6 0 3 3 0 0 0-6 0z" />
                </svg>
            </button>
            <div className="bg-black bg-opacity-50 min-h-screen flex flex-col items-center justify-center py-16 space-y-10 px-4">
                <Header />
                <GameCode code={roomCode} />
                <PlayerList players={players} />
                {isConnected && client.current && (
                    <TokenSelector players={players} roomCode={roomCode} client={client.current} />
                )}
                {allReady ? (
                    <button
                        onClick={handleStartGame}
                        className="mt-6 px-8 py-3 bg-green-500 hover:bg-green-600 text-white text-lg font-bold rounded-full shadow-lg transition-all duration-300"
                    >
                        Empezar partida
                    </button>
                ) : (
                    <button
                        disabled
                        className="mt-6 px-8 py-3 bg-gray-400 text-white text-lg font-bold rounded-full shadow-lg cursor-not-allowed"
                    >
                        Esperando jugadores...
                    </button>
                )}
                <ToastContainer position="top-center" autoClose={3000} />
            </div>

            {showSettings && (
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
                            onClick={() => setShowSettings(false)}
                            className="w-full bg-yellow-400 hover:bg-yellow-500 py-2 rounded-xl font-bold"
                        >
                            Cerrar
                        </button>
                    </div>
                </div>
            )}
            {showReconnectModal && (
                <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50">
                    <div className="bg-white rounded-lg shadow-xl p-6 max-w-sm text-center">
                        <h2 className="text-xl font-bold text-yellow-600 mb-4">¡Ya estás en una partida!</h2>
                        <p className="text-gray-700 mb-6">¿Quieres reconectarte a la sala <strong>{pendingCode}</strong> o salir de ella y crear una nueva?</p>
                        <div className="flex justify-center gap-4">
                            <button
                                onClick={handleReconnect}
                                className="px-4 py-2 bg-green-500 hover:bg-green-600 text-white font-semibold rounded-full shadow-md transition duration-300"
                            >
                                Reconectar
                            </button>
                            <button
                                onClick={handleNewGame}
                                className="px-4 py-2 bg-red-500 hover:bg-red-600 text-white font-semibold rounded-full shadow-md transition duration-300"
                            >
                                Nueva partida
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
