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

                stompClient.subscribe('/topic/CreateGame', (message) => {
                    const data = JSON.parse(message.body);
                    console.log('Datos recibidos:', data);

                    if (data.success) {
                        setRoomCode(data.codeGame);

                        if (data.gamePlayer) {
                            setPlayers((prevPlayers) => [
                                ...prevPlayers,
                                { nickname: data.gamePlayer.nickname, token: data.gamePlayer.token || '' }
                            ]);
                        }
                    } else {
                        console.error('Error al crear la partida:', data);
                    }
                });

                stompClient.subscribe('/topic/SelectPieceGame', (message) => {
                    console.log('Mensaje recibido:', message.body);
                    const data = JSON.parse(message.body);
                    console.log('Respuesta selección de ficha:', data);
                    if (data.success) {
                        const updatedPlayer = data.gamePlayer;
                        const selectedPiece = data.piece; 

                        setPlayers((prevPlayers) =>
                            prevPlayers.map((p) =>
                                p.nickname === updatedPlayer.nickname
                                    ? { ...p, token: selectedPiece }
                                    : p
                            )
                        );
                        console.log('Jugador actualizado:', updatedPlayer);
                    } else {
                        console.error('Error al seleccionar ficha:', data.error);
                    }
                });

                const nickname = Cookies.get('nickname');
                if (nickname) {
                    stompClient.publish({
                        destination: '/Game/Create',
                        body: nickname,
                    });
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
