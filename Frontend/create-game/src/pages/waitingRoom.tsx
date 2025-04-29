import { useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';
import { X } from 'lucide-react'; 
import Cookies from 'js-cookie'
import Header from '../components/header';
import GameCode from '../components/gameCode';
import PlayerList from '../components/playerList';
import TokenSelector from '../components/TokenSelector';

export default function WaitingRoom() {
    const [players, setPlayers] = useState<any[]>([]);
    const [roomCode, setRoomCode] = useState('');
    const [isConnected, setIsConnected] = useState(false);

    const client = useRef<Client | null>(null);

    useEffect(() => {
        const stompClient = new Client({
            brokerURL: 'ws://localhost:8003/app',
            reconnectDelay: 1000,
            onConnect: () => {
                console.log('Conectado al WebSocket');
                setIsConnected(true);

                stompClient.subscribe('/topic/CreateGame', (message) => {
                    const data = JSON.parse(message.body);
                    console.log('Datos recibidos:', data);
                    if (data.success) {
                        setRoomCode(data.codeGame);
                        Cookies.set('gameCode',data.codeGame)
                        if (data.gamePlayers) {
                            setPlayers(data.gamePlayers.map((player: any) => ({
                                nickname: player.nickName,
                                token: player.namePiece || '',
                            })));
                        }
                    } else {
                        console.error('Error al crear la partida:', data.error);
                    }
                });

                const gameCode = Cookies.get('gameCode');
                stompClient.subscribe(`/topic/game/${gameCode}`, (message) => {
                    const data = JSON.parse(message.body);
                    console.log('Datos recibidos al unirse:', data);

                    if (data.success && data.gamePlayers) {
                        setPlayers(data.gamePlayers.map((player: any) => ({
                            nickname: player.nickName,
                            token: player.namePiece || '',
                        })));
                    } else {
                        console.error('Error al unirse a la partida:', data.error);
                    }
                });

                stompClient.subscribe(`/topic/SelectedPieceGame/${gameCode}`, (message) => {
                    const data = JSON.parse(message.body);
                    console.log('Mensaje recibido:', data);
                    if (data.success) {
                        const updatedPlayer = data.gamePlayer;

                        setPlayers((prevPlayers) =>
                            prevPlayers.map((p) =>
                                p.nickname === updatedPlayer.nickName
                                    ? { ...p, token: updatedPlayer.namePiece }
                                    : p
                            )
                        );
                        console.log('Jugador actualizado:', updatedPlayer.namePiece);
                    } else {
                        console.error('Error al seleccionar ficha:', data.error);
                    }
                });

                stompClient.subscribe(`/topic/Exit/${gameCode}`, (message) => {
                    const data = JSON.parse(message.body);
                    console.log('Datos recibidos al salir:', data);

                    if (data.success && data.gamePlayers) {
                        setPlayers(data.gamePlayers.map((player: any) => ({
                            nickname: player.nickName,
                            token: player.namePiece || '',
                        })));
                    } else {
                        console.error('Error al recibir actualización tras salida:', data.error);
                    }
                });
                const nickname = Cookies.get('nickname');
                if (nickname) {
                    if (!gameCode) {
                        stompClient.publish({
                            destination: '/Game/Create',
                            body: nickname,
                        });
                    } else {
                        setRoomCode(gameCode);
                        Cookies.set('gameCode',gameCode)
                        const gamePlayer = {
                            idGame: parseInt(gameCode),
                            nickName: nickname,
                        };

                        stompClient.publish({
                            destination: '/Game/JoinGame',
                            body: JSON.stringify(gamePlayer),
                        });
                    }
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
    }, []);


    const handleStartGame = () => {
        console.log('¡La partida comienza!', roomCode);
    };

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
        window.location.href = 'http://localhost:3000/menu';
    };



    return (
        <div
            className="min-h-screen bg-cover bg-center text-white"
            style={{ backgroundImage: "url('/Fichas/Fondo.jpg')" }}
        >
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
                <button
                    onClick={handleStartGame}
                    className="mt-6 px-8 py-3 bg-green-500 hover:bg-green-600 text-white text-lg font-bold rounded-full shadow-lg transition-all duration-300"
                >
                    Empezar partida
                </button>
            </div>
        </div>
    );
}
