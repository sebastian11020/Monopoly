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
    nickName: string;
    type?: 'PropertyCard' | 'TransportCard' | string;
    statePosition?: 'DISPONIBLE' | 'COMPRADA' | string;
    message?: string;
    gamePlayers: any[];
    [key: string]: any;
}

const GameView = () => {
    const background = '/Fichas/Fondo.jpg';
    const nickname = Cookies.get('nickname');
    const codeGame = Cookies.get('gameCode');
    const stompClientRef = useRef<Client | null>(null);
    const [gameState, setGameState] = useState<GameState | null>(null);
    const [propiedadSeleccionada, setPropiedadSeleccionada] = useState<string | null>(null);
    const [jugadorSeleccionado, setJugadorSeleccionado] = useState<Player | null>(null);
    const [dadosLocales, setDadosLocales] = useState<{dice1: number, dice2: number} | null>(null);
    const [buyPrompt, setBuyPrompt] = useState<null | { message: string }>(null);
    const [pendingBuyPrompt, setPendingBuyPrompt] = useState<null | { message: string, pieceName: string }>(null);

    useEffect(() => {
        const stompClient = new Client({
            brokerURL: 'ws://localhost:8004/app',
            reconnectDelay: 5000,
            onConnect: () => {
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
                    const currentUser = Cookies.get('nickname');
                    try {
                        const data: GameState = JSON.parse(message.body);
                        setGameState(data);
                        setDadosLocales(null);
                        const currentPlayer = data.gamePlayers.find(player => player.nickName === currentUser);
                        console.log("CurrentPlayer",currentPlayer)
                        if (
                            currentPlayer &&
                            (currentPlayer.type === "PropertyCard" || currentPlayer.type === "TransportCard") &&
                            currentPlayer.statePosition === "DISPONIBLE" &&
                            data.message
                        ) {
                            console.log("Seteando modal de compra para: ", currentPlayer.piece.name, " - ", data.message, " -")
                            setPendingBuyPrompt({ message: data.message, pieceName: currentPlayer.piece.name });
                        }

                        setDadosLocales(null);
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
                setDadosLocales({ dice1, dice2 }); // Guárdalos temporalmente
                stompClientRef.current.publish({
                    destination: '/Game/RollDice',
                    body: JSON.stringify({ codeGame: codeGame ?? '', dice1, dice2 }),
                });

            }
        };

        window.addEventListener('keydown', handleKeyDown);

        return () => {
            window.removeEventListener('keydown', handleKeyDown);
        };
    }, [jugadorActivo, nickname, codeGame]);
/*
    useEffect(() => {
        const currentUser = nickname?.trim().toLowerCase();
        const isMyTurn = jugadorActivo?.nickName.trim().toLowerCase() === currentUser;
        console.log(isMyTurn)
        if (!isMyTurn && buyPrompt) {
            console.log("⛔ Ya no es tu turno, cerrando modal de compra");
            setBuyPrompt(null);
            setPendingBuyPrompt(null);
        }
    }, [jugadorActivo, buyPrompt, nickname]);
*/
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
            {buyPrompt && (
                <div className="fixed inset-0 flex items-end justify-start z-50 p-8 pointer-events-none">
                    <div className="relative flex items-end gap-4 pointer-events-auto">
                        {/* Imagen del personaje */}
                        <img
                            src="/assets/Mr Monopoly.png"
                            alt="Señor Monopoly"
                            className="w-40 h-40 md:w-52 md:h-52 object-contain drop-shadow-xl"
                        />

                        {/* Globo de diálogo */}
                        <div className="absolute -top-40 transform-translate-x-1/2 bg-white rounded-2xl shadow-xl border border-gray-300 p-6 max-w-md text-black">
                            {/* Puntero del globo (flecha) */}
                            <div className="absolute -bottom-3 left-12 w-6 h-6 bg-white rotate-45 border-l border-b border-gray-300"></div>
                            <h2 className="text-lg font-bold mb-2">¡Oye, joven!</h2>
                            <p className="mb-4">{buyPrompt.message}</p>
                            <div className="flex justify-end gap-3">
                                <button
                                    onClick={() => {
                                        console.log("Compra aceptada");
                                        setBuyPrompt(null);

                                        if (stompClientRef.current) {
                                            stompClientRef.current.publish({
                                                destination: '/Game/Buy',
                                                body: JSON.stringify({
                                                    codeGame,
                                                    nickName: nickname,
                                                    buy: true,
                                                }),
                                            });
                                        }
                                    }}
                                    className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-xl transition"
                                >
                                    Comprar
                                </button>
                                <button
                                    onClick={() => {
                                        console.log("Compra cancelada");
                                        setBuyPrompt(null);

                                        if (stompClientRef.current) {
                                            stompClientRef.current.publish({
                                                destination: '/Game/Buy',
                                                body: JSON.stringify({
                                                    codeGame,
                                                    nickName: nickname,
                                                    buy: false,
                                                }),
                                            });
                                        }
                                    }}
                                    className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-xl transition"
                                >
                                    Cancelar
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
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
                            dice1={dadosLocales?.dice1 ?? jugadorActivo?.dice1 ?? 0}
                            dice2={dadosLocales?.dice2 ?? jugadorActivo?.dice2 ?? 0}
                            onPieceMovementEnd={(pieceName) => {
                                console.log("🕵️ Verificando pieza:", pieceName, "vs", pendingBuyPrompt?.pieceName);
                                console.log("Tipos:", typeof pieceName, typeof pendingBuyPrompt?.pieceName);
                                console.log("Igualdad estricta:", pieceName === pendingBuyPrompt?.pieceName);
                                if (pendingBuyPrompt && pieceName === pendingBuyPrompt.pieceName) {
                                    console.log("✅ Coincidencia de pieza, mostrando modal...",pendingBuyPrompt.pieceName,pieceName);
                                    setBuyPrompt({ message: pendingBuyPrompt.message });
                                    setPendingBuyPrompt(null);
                                }
                            }}
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