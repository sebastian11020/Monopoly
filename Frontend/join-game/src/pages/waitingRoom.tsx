import { useEffect, useState } from 'react';
import io from 'socket.io-client';
import Header from '../components/header';
import GameCode from '../components/gameCode';
import PlayerList from '../components/playerList';
import TokenSelector from '../components/TokenSelector';
import waitingRoomCode from '../services/waitingRooom'

const socket = io('http://localhost:3000');

export default function WaitingRoom() {
    const [players, setPlayers] = useState<any[]>([]);
    const [roomCode, setRoomCode] = useState('');

    useEffect(() => {
        const fetchRoomCode = async () => {
            const code = await waitingRoomCode();
            if (code) {
                setRoomCode(code);
            }
        };
        fetchRoomCode();
        socket.on('player-joined', (player) => {
            setPlayers(prev => [...prev, player]);
            console.log(player)
        });

        socket.on('token-selected', ({ playerId, token }) => {
            setPlayers(prev =>
                prev.map(p =>
                    p.id === playerId ? { ...p, token } : p
                )
            );
        });

        return () => {
            socket.off('player-joined');
            socket.off('token-selected');
        };
    }, []);

    const handleStartGame = () => {
        console.log('¡La partida comienza!');
        socket.emit('start-game', { roomCode });
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
