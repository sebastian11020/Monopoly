import { useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';
import { useNavigate } from 'react-router-dom';
import { X } from 'lucide-react';
import Cookies from 'js-cookie';
import Header from '../components/header';
import GameCode from '../components/gameCode';
import PlayerList from '../components/playerList';
import TokenSelector from '../components/TokenSelector';

export default function WaitingRoomJoin() {
    const [players, setPlayers] = useState<any[]>([]);
    const [roomCode, setRoomCode] = useState('');
    const [isConnected, setIsConnected] = useState(false);
    const [isReady, setIsReady] = useState(false);
    const client = useRef<Client | null>(null);
    const [showEndModal, setShowEndModal] = useState<boolean>(false);
    const [endMessage, setEndMessage] = useState<string>('');
    const history = useNavigate();
    const audioRef = useRef<HTMLAudioElement | null>(null);
    const [volume, setVolume] = useState(0.05);
    const [isMuted, setIsMuted] = useState(false);
    const [showSettings, setShowSettings] = useState<boolean>(false);
    const nickname = Cookies.get('nickname');
    const [showReconnectModal, setShowReconnectModal] = useState(false);
    const [pendingCode, setPendingCode] = useState('');

    useEffect(() => {
        const stompClient = new Client({
            brokerURL: 'ws://localhost:8004/app',
            reconnectDelay: 5000,
            onConnect: () => {
                console.log('Conectado al WebSocket');
                setIsConnected(true);
                const gameCode = Cookies.get('gameCode');
                stompClient.subscribe(`/topic/JoinGame/${gameCode}`, (message) => {
                    const data = JSON.parse(message.body);
                    console.log('Datos recibidos al unirse:', data);
                    if(data.success) {
                        if (data.stateGame === 'EN_ESPERA') {
                            setRoomCode(data.codeGame);
                            if (data.gamePlayers) {
                                setPlayers(data.gamePlayers.map((player: any) => ({
                                    nickname: player.nickName,
                                    token: player.namePiece || '',
                                    state: player.state,
                                })));
                            }
                        } else {
                            setEndMessage(data.confirm || 'La partida ya ha finalizado.');
                            setShowEndModal(true);
                            Cookies.remove('gameCode');
                            return;
                        }
                    } else if (data.error?.includes('El jugador ya encuentra registrado')) {
                    setPendingCode(data.codeGame);
                    setShowReconnectModal(true);
                }
                });
                stompClient.subscribe(`/topic/StartGame/${gameCode}`, (message) => {
                    const data = JSON.parse(message.body);
                    if (data.success) {
                        console.log('La partida ha comenzado. Redirigiendo...');
                        navigate('/game');
                    }
                });
                stompClient.subscribe(`/topic/SelectPieceGame/${gameCode}`, (message) => {
                    const data = JSON.parse(message.body);
                    console.log('Respuesta selección de ficha:', data);
                    if (data.success) {
                        const updatedPlayer = data.gamePlayer;
                        console.log('Actualizando ficha:', updatedPlayer);
                        setPlayers((prevPlayers) =>
                            prevPlayers.map((p) =>
                                p.nickname === updatedPlayer.nickName
                                    ? { ...p, token: updatedPlayer.namePiece }
                                    : p
                            )
                        );
                    } else {
                        console.error('Error al seleccionar ficha:', data.error);
                    }
                });
                stompClient.subscribe(`/topic/Exit/${gameCode}`, (message) => {
                    const data = JSON.parse(message.body);
                    console.log('Datos recibidos al salir:', data);
                    if (data.stateGame === 'FINALIZADO') {
                        console.log('Mostrando alerta de partida finalizada');
                        setEndMessage(data.confirm || 'La partida ha sido finalizada por el creador.');
                        setShowEndModal(true);
                        Cookies.remove('gameCode');
                        return;
                    }
                    if (data.success && data.gamePlayers) {
                        setPlayers(data.gamePlayers.map((player: any) => ({
                            nickname: player.nickName,
                            token: player.namePiece || '',
                            state: player.state,
                        })));
                    } else {
                        console.error('Error al recibir actualización tras salida:', data.error);
                    }
                });


                stompClient.subscribe(`/topic/ChangeStatePlayer/${gameCode}`, (message) => {
                    const data = JSON.parse(message.body);console.log('Estado actualizado:', data);
                    if (data.success && data.gamePlayers) {
                        setPlayers(data.gamePlayers.map((p: any) => ({
                            nickname: p.nickName,
                            token: p.namePiece || '',
                            state: p.state,
                        })));
                    }
                });

                const nickname = Cookies.get('nickname');

                if (nickname && gameCode) {
                    const gamePlayer = {
                        idGame: parseInt(gameCode),
                        nickName: nickname,
                    };

                    stompClient.publish({
                        destination: '/Game/JoinGame',
                        body: JSON.stringify(gamePlayer),
                    });

                    console.log('Intentando unirse con:', gamePlayer);
                } else {
                    console.error('No se encontró nickname o roomCode');
                }
            }
        });

        stompClient.activate();
        client.current = stompClient;

        return () => {
            if (client.current && client.current.active) {
                client.current.deactivate();
            }
        };
    }, [roomCode]);

    useEffect(() => {
        const handleBeforeUnload = () => {
            handleExit();
        };

        window.addEventListener('beforeunload', handleBeforeUnload);
        return () => window.removeEventListener('beforeunload', handleBeforeUnload);
    }, []);


    useEffect(() => {
        if (audioRef.current) {
            audioRef.current.volume = isMuted ? 0 : volume;
            audioRef.current.loop = true;
            audioRef.current.play().catch((e) => console.error('Audio error:', e));
        }
    }, [volume, isMuted]);

    const handleReadyToggle = () => {
        const gameCode = Cookies.get('gameCode');
        const nickName = Cookies.get('nickname');
        const selectSound = new Audio('/sounds/unirse.mp3');
        selectSound.volume = 0.6;
        selectSound.play().catch(err => console.error('Error reproduciendo sonido:', err));
        if (!gameCode || !nickName || !client.current || !client.current.connected) {
            console.error('No se puede enviar estado de listo. Faltan datos o WebSocket no conectado.');
            return;
        }

        const changeState = {
            codeGame: parseInt(gameCode),
            nickName: nickName,
            state: !isReady,
        };

        client.current.publish({
            destination: '/Game/ChangeState',
            body: JSON.stringify(changeState),
        });

        console.log('Estado de listo enviado:',changeState);

        setIsReady(!isReady);
    };
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

            console.log('🚪 Salida enviada de la partida previa:', exitGame);
        }

        Cookies.remove('gameCode');
        setShowReconnectModal(false);
        navigate('/page-code');
    };

    const navigate = useNavigate();

    const handleExit = async () => {
        const gameCode = Cookies.get('gameCode');
        const nickName = Cookies.get('nickname');

        if (nickName && gameCode && client.current && client.current.connected) {
            try {
                const exitGame = {
                    nickName: nickName,
                    codeGame: parseInt(gameCode),
                };

                client.current.publish({
                    destination: '/Game/Exit',
                    body: JSON.stringify(exitGame),
                });
                console.log('Mensaje de salida enviado por WebSocket:', exitGame);

            } catch (error) {
                console.error('Error enviando la salida por WebSocket:', error);
            }
        } else {
            console.error('Faltan datos o no está conectado el WebSocket.');
        }
        Cookies.remove('gameCode');
        navigate('/page-code');
    };


    return (
        <div
            className="min-h-screen bg-cover bg-center text-white"
            style={{ backgroundImage: "url('/Fichas/Fondo.jpg')" }}
        >
            <audio ref={audioRef} src="/sounds/waiting-room.mp3" autoPlay />
            <button
                onClick={handleExit}
                className="absolute top-6 right-6 bg-yellow-300 hover:bg-yellow-400 text-black rounded-full w-10 h-10 flex items-center justify-center shadow-lg transform transition-transform duration-300 hover:scale-110"
            >
                <X size={24} strokeWidth={3} />
            </button>
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
                <button
                    onClick={handleReadyToggle}
                    className={`mt-6 px-8 py-3 ${isReady ? 'bg-red-500 hover:bg-red-600' : 'bg-green-500 hover:bg-green-600'} text-white text-lg font-bold rounded-full shadow-lg transition-all duration-300`}
                >
                    {isReady ? 'Cancelar' : '¡Listo!'}
                </button>
            </div>
            {showEndModal && (
                <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50">
                    <div className="bg-white rounded-lg shadow-xl p-6 max-w-sm text-center">
                        <h2 className="text-xl font-bold text-red-600 mb-4">Partida finalizada</h2>
                        <p className="text-gray-700 mb-6">{endMessage}</p>
                        <button
                            onClick={() => {
                                setShowEndModal(false);
                                history('/page-code');
                            }}
                            className="px-6 py-2 bg-yellow-400 hover:bg-yellow-500 text-black font-semibold rounded-full shadow-md transition duration-300"
                        >
                            Volver a unirse
                        </button>
                    </div>
                </div>
            )}
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
                        <p className="text-gray-700 mb-6">¿Quieres reconectarte a la sala <strong>{pendingCode}</strong> o salir de ella y unirte a una nueva?</p>
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
                                Unirse
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
