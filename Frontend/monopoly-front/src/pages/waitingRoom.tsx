import { useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';
import { X } from 'lucide-react';
import Cookies from 'js-cookie';
import Header from '../components/header';
import GameCode from '../components/gameCode';
import PlayerList from '../components/playerList';
import TokenSelector from '../components/TokenSelector';
import { useNavigate } from 'react-router-dom';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

export default function WaitingRoom() {
    const [players, setPlayers] = useState<any[]>([]);
    const [roomCode, setRoomCode] = useState('');
    const [isConnected, setIsConnected] = useState(false);
    const client = useRef<Client | null>(null);
    const history = useNavigate();

    const nickname = Cookies.get('nickname');

    useEffect(() => {
        const stompClient = new Client({
            brokerURL: 'ws://localhost:8003/app',
            reconnectDelay: 1000,
            onConnect: () => {
                console.log('Conectado al WebSocket');
                setIsConnected(true);

                const nickname = Cookies.get('nickname');
                const savedCode = Cookies.get('gameCode');

                stompClient.subscribe('/topic/CreateGame', (message) => {
                    const data = JSON.parse(message.body);
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
                    }
                });

                if (nickname) {
                    if (!savedCode) {
                        stompClient.publish({
                            destination: '/Game/Create',
                            body: nickname,
                        });
                    } else {
                        setRoomCode(savedCode);
                    }
                }
            },
            onStompError: (frame) => {
                console.error('Error STOMP:', frame);
                toast.error('Error al conectar con la sala de espera');
            }
        });

        stompClient.activate();
        client.current = stompClient;

        return () => {
            if (client.current?.active) {
                client.current.deactivate();
            }
        };
    }, []);

    useEffect(() => {
        if (!roomCode || !client.current?.connected) return;

        const stompClient = client.current;
        const nickname = Cookies.get('nickname');

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
            console.log('SelectPieceGame:', data);
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
            console.log('Estado actualizado:', data);

            if (data.success && data.gamePlayers) {
                setPlayers(data.gamePlayers.map((p: any) => ({
                    nickname: p.nickName,
                    token: p.namePiece || '',
                    state: p.state,
                })));
            }
        });

        stompClient.subscribe(`/topic/Exit/${roomCode}`, (message) => {
            const data = JSON.parse(message.body);
            console.log('Exit:', data);
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
        </div>
    );
}
