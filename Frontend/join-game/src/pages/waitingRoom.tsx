import { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import Cookies from 'js-cookie';
import Header from '../components/header';
import GameCode from '../components/gameCode';
import PlayerList from '../components/playerList';
import TokenSelector from '../components/TokenSelector';

export default function WaitingRoom() {
    const [players, setPlayers] = useState<any[]>([]);
    const [roomCode, setRoomCode] = useState('');
    const [message, setMessage] = useState('');
    const client = new Client();

    useEffect(() => {
        client.configure({
            brokerURL: 'ws://localhost:8080/app/websocket',
            onConnect: () => {
                console.log('Conectado al WebSocket');

                client.subscribe('/topic/CreateGame', (message) => {
                    const data = JSON.parse(message.body);
                    console.log('Datos recibidos:', data);

                    if (data.success) {
                        setMessage(data.confirm);
                        setRoomCode(data.codeGame);

                        if (data.player) {
                            setPlayers((prevPlayers) => [...prevPlayers, data.player]);
                        }
                    } else {
                        console.error('Error al crear la partida:', data);
                    }
                });

                const nickname = Cookies.get('nickname');
                if (nickname) {
                    client.publish({
                        destination: '/Game/Create',
                        body: nickname, 
                    });
                }
            },
            reconnectDelay: 5000,
        });

        client.activate();

        return () => {
            if (client.active) {
                client.deactivate();
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

                {message && <p className="text-green-500">{message}</p>}

                <GameCode code={roomCode} />

                <PlayerList players={players} />

                <TokenSelector players={players} roomCode={roomCode} />

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