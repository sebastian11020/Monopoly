import {useEffect, useRef, useState} from 'react';
import Board from '../components/board';
import PlayerModal from '../components/PlayerInfoModal';
import PropertyModal from '../components/PropertyModal';
import PlayerList from '../components/PlayerListGame';
import PlayerSidebar from '../components/sideBar';
import Cookies from 'js-cookie';
import { Client } from '@stomp/stompjs';

interface Piece {
    id: number;
    name: string;
}

interface Turn {
    id: number;
    game: number;
    turn: number;
    active: boolean;
}

interface Player {
    codeGame: number;
    nickName: string;
    dice1: number;
    dice2: number;
    position: number;
    cash: number;
    piece: Piece;
    turn: Turn;
}

interface GameState {
    success: boolean;
    confirm: string;
    codeGame: number;
    stateGame: string;
    gamePlayers: Player[];
}

const GameView = () => {
    const background = '/Fichas/Fondo.jpg';
    const nickname = Cookies.get('nickname');
    const codeGame = Cookies.get('gameCode');
    const stompClientRef = useRef<Client | null>(null);
    const [gameState, setGameState] = useState<GameState | null>(null);
    const [propiedadSeleccionada, setPropiedadSeleccionada] = useState<string | null>(null);
    const [jugadorSeleccionado, setJugadorSeleccionado] = useState<Player | null>(null);

    useEffect(() => {
        const stompClient = new Client({
            brokerURL: 'ws://localhost:8004/app',
            reconnectDelay: 5000,
            onConnect: () => {
                console.log("Conexión WebSocket establecida.");

                stompClient.subscribe(`/topic/StartGame/${codeGame}`, (message) => {
                    console.log("[StartGame] Mensaje recibido:", message.body);
                    try {
                        const data: GameState = JSON.parse(message.body);
                        setGameState(data);
                    } catch (error) {
                        console.error("Error al parsear StartGame:", error);
                    }
                });
                stompClient.subscribe(`/topic/RollDice/${codeGame}`, (message) => {
                    console.log("[RollDice] Mensaje recibido:", message.body);
                    try {
                        const data: GameState = JSON.parse(message.body);
                        setGameState(data);
                    } catch (error) {
                        console.error("Error al parsear RollDice:", error);
                    }
                });

                stompClient.publish({
                    destination: '/Game/StartGame',
                    body: codeGame,
                });
            },
            onWebSocketError: (error) => {
                console.error("Error de WebSocket:", error);
            },
            onDisconnect: () => {
                console.log("Conexión WebSocket desconectada.");
            },
        });
        stompClientRef.current = stompClient;
        stompClient.activate();

        return () => {
            stompClient.deactivate();
        };
    }, [codeGame]);

    const otherPlayers = Array.isArray(gameState?.gamePlayers)
        ? gameState!.gamePlayers.filter((j) => j.nickName !== nickname)
        : [];
    const jugadorActivo = Array.isArray(gameState?.gamePlayers)
        ? gameState!.gamePlayers.find(j => j.turn.active)
        : null;

    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            if (
                e.code === 'Space' &&
                stompClientRef.current &&
                jugadorActivo?.nickName.trim().toLowerCase() === nickname?.trim().toLowerCase()
            ) {
                const dice1 = Math.floor(Math.random() * 6) + 1;
                const dice2 = Math.floor(Math.random() * 6) + 1;

                console.log("Tirando dados...", { dice1, dice2 });

                const payload = {
                    codeGame: codeGame ?? '',
                    dice1,
                    dice2
                };

                stompClientRef.current.publish({
                    destination: '/Game/RollDice',
                    body: JSON.stringify(payload),
                });
            }
        };

        window.addEventListener('keydown', handleKeyDown);

        return () => {
            window.removeEventListener('keydown', handleKeyDown);
        };
    }, [jugadorActivo, nickname, codeGame]);

    return (
        <div
            className="w-full h-screen flex flex-col bg-cover bg-center text-white"
            style={{ backgroundImage: `url(${background})` }}
        >
            {propiedadSeleccionada && (
                <PropertyModal
                    propiedadSeleccionada={propiedadSeleccionada}
                    onClose={() => setPropiedadSeleccionada(null)}
                />
            )}

            {jugadorSeleccionado && (
                <PlayerModal
                    jugador={jugadorSeleccionado}
                    onClose={() => setJugadorSeleccionado(null)}
                />
            )}

            <PlayerList players={otherPlayers} onSelect={setJugadorSeleccionado} />

            <div className="flex flex-1 overflow-hidden relative">
                <div className="flex justify-center items-center w-2/3 p-4">
                    <div className="rounded-2xl shadow-[0_0_30px_rgba(0,0,0,0.5)]">
                        <Board
                            players={gameState?.gamePlayers.map(p => ({
                                namePiece: p.piece.name,
                                position: p.position,
                            })) || []}
                            dice1={jugadorActivo?.dice1 ?? 0}
                            dice2={jugadorActivo?.dice2 ?? 0}
                        />
                    </div>
                </div>
                <PlayerSidebar
                    currentPlayer={gameState?.gamePlayers.find((j) => j.nickName === nickname) ?? null}
                />
            </div>
        </div>
    );
};

export default GameView;