import { useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';
import Cookies from 'js-cookie';
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
            reconnectDelay: 5000,
            onConnect: () => {
                console.log('Conectado al WebSocket');
                setIsConnected(true);

                stompClient.subscribe('/topic/JoinGame', (message) => {
                    const data = JSON.parse(message.body);
                    console.log('Datos recibidos al unirse:', data);
                    if (data.success) {
                        setRoomCode(data.codeGame);
                        if (data.gamePlayers) {
                            setPlayers(data.gamePlayers.map((player: any) => ({
                                nickname: player.nickName,
                                token: player.namePiece || '',
                            })));
                        }
                    } else {
                        console.error('Error al unirse a la partida:', data.error);
                    }
                });

                stompClient.subscribe('/topic/SelectPieceGame', (message) => {
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

                const nickname = Cookies.get('nickname');
                const gameCode = Cookies.get('gameCode');
                

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
    }, []);

    const handleStartGame = () => {
        console.log('¡Esperando que el host inicie la partida!', roomCode);
    };

    return (
        <div
            className="min-h-screen bg-cover bg-center text-white"
            style={{ backgroundImage: "url('/Fichas/Fondo.jpg')" }}
        >
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
                    ¡Listo!
                </button>
            </div>
        </div>
    );
}
