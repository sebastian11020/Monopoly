import {useEffect, useRef, useState} from 'react';
import Board from '../components/board';
import PlayerModal from '../components/PlayerInfoModal';
import PropertyModal from '../components/PropertyModal';
import PlayerList from '../components/PlayerListGame';
import PlayerSidebar from '../components/sideBar';
import Cookies from 'js-cookie';
import { Client } from '@stomp/stompjs';
import {Player,GameState,Buy} from '../utils/type'


const GameView = () => {
    const background = '/Fichas/Fondo.jpg';
    const nickname = Cookies.get('nickname');
    const codeGame = Cookies.get('gameCode');
    const stompClientRef = useRef<Client | null>(null);
    const [isOk,setOk] = useState<boolean>(false)
    const [gameState, setGameState] = useState<GameState | null>(null);
    const [propiedadSeleccionada, setPropiedadSeleccionada] = useState<string | null>(null);
    const [jugadorSeleccionado, setJugadorSeleccionado] = useState<Player | null>(null);
    const [dadosLocales, setDadosLocales] = useState<{dice1: number, dice2: number} | null>(null);
    const [buyPrompt, setBuyPrompt] = useState<null | { message: string }>(null);
    const [pendingBuyPrompt, setPendingBuyPrompt] = useState<null | { message: string, pieceName: string }>(null);
    const [pendingNotifyPayPrompt, setPendingNotifyPayPrompt] = useState<null | { message: string, pieceName: string }>(null);
    const [notifyPayPrompt, setNotifyPayPrompt] = useState<null | { message: string }>(null);


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
                            (currentPlayer.type === "PROPERTY" || currentPlayer.type === "TRANSPORT" || currentPlayer.type === "SERVICE") &&
                            currentPlayer.statePosition === "DISPONIBLE" &&
                            data.message
                        ) {
                            console.log("Seteando modal de compra para: ", currentPlayer.piece.name, " - ", data.message, " -")
                            setPendingBuyPrompt({ message: data.message, pieceName: currentPlayer.piece.name });
                        }else if(currentPlayer && (currentPlayer.type === "TAXES" || currentPlayer.type === "Card")
                            && currentPlayer.statePosition === "ESPECIAL" && data.message) {
                            console.log("Seteando Especial: ", currentPlayer.piece.name, " - ", data.message, " -")
                            setPendingNotifyPayPrompt({message: data.message,pieceName: currentPlayer.piece.name})
                        }
                        setDadosLocales(null);
                    } catch (error) {
                        console.error("Error al parsear RollDice:", error);
                    }
                });
                stompClient.subscribe(`/topic/Buy/${codeGame}`, (message) => {
                    console.log("[Buy] Mensaje recibido:", message.body);
                    try {
                        const data:Buy = JSON.parse(message.body);
                        if(nickname===data.nickName){
                            setNotifyPayPrompt({message: data.message})
                            if(!isOk){
                                setTimeout(() => {
                                    if (stompClientRef.current) {
                                        stompClientRef.current.publish({
                                            destination: '/Game/NextTurn',
                                            body: codeGame,
                                        });
                                        setNotifyPayPrompt(null);
                                    }
                                }, 10000);
                            }
                        }
                    } catch (error) {
                        console.error("Error al parsear Buy:", error);
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
                setDadosLocales({ dice1, dice2 });
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
                <div className="fixed inset-0 flex items-end justify-start z-50 p-8 font-['Press_Start_2P'] pointer-events-none">
                    <div className="relative flex items-end gap-6 pointer-events-auto animate-[bounce-in_0.4s_ease-out]">
                        {/* Imagen del personaje */}
                        <img
                            src="/assets/Mr Monopoly.png"
                            alt="Señor Monopoly"
                            className="w-40 h-40 md:w-52 md:h-52 object-contain drop-shadow-[0_0_20px_rgba(255,255,255,0.4)]"
                        />

                        {/* Globo de diálogo */}
                        <div className="absolute -top-44 left-36 bg-white text-black rounded-2xl shadow-2xl border-4 border-yellow-400 px-6 py-4 max-w-lg z-10">
                            {/* Puntero del globo (flecha) */}
                            <div className="absolute -bottom-4 left-12 w-6 h-6 bg-white rotate-45 border-l-4 border-b-4 border-yellow-400"></div>

                            <h2 className="text-base md:text-lg text-yellow-600 font-bold mb-3 drop-shadow">¡Oye, joven!</h2>
                            <p className="text-sm md:text-base mb-5">{buyPrompt.message}</p>

                            {/* Botones */}
                            <div className="flex justify-end gap-4">
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
                                    className="bg-green-600 hover:bg-green-700 text-white px-5 py-2 rounded-lg shadow-md text-xs md:text-sm transition-all"
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
                                    className="bg-red-600 hover:bg-red-700 text-white px-5 py-2 rounded-lg shadow-md text-xs md:text-sm transition-all"
                                >
                                    Cancelar
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
            {notifyPayPrompt && (
                <div className="fixed inset-0 flex items-end justify-center z-50 p-8 font-['Press_Start_2P'] pointer-events-none">
                    <div className="relative flex items-end gap-4 pointer-events-auto animate-[fade-in-up_0.4s_ease-out]">
                        {/* Imagen del personaje */}
                        <img
                            src="/assets/Mr-Monopoly-Sad.png"
                            alt="Mr. Monopoly"
                            className="w-32 h-32 md:w-40 md:h-40 object-contain drop-shadow-[0_0_20px_rgba(255,255,255,0.4)]"
                        />

                        {/* Globo de notificación */}
                        <div className="relative bg-white text-black rounded-2xl shadow-2xl border-4 border-yellow-400 px-6 py-4 max-w-xl z-10">
                            {/* Puntero del globo */}
                            <div className="absolute -bottom-4 left-10 w-6 h-6 bg-white rotate-45 border-l-4 border-b-4 border-yellow-400"></div>

                            <h2 className="text-base md:text-lg text-yellow-600 font-bold mb-3 drop-shadow">
                                Señor Monopoly dice:
                            </h2>
                            <p className="text-sm md:text-base mb-4">
                                {notifyPayPrompt.message}
                            </p>

                            <div className="flex justify-end">
                                <button
                                    onClick={() => {
                                        setTimeout(()=>{
                                            stompClientRef.current?.publish({
                                                destination: '/Game/NextTurn',
                                                body: codeGame
                                            })
                                        },100)
                                        setNotifyPayPrompt(null)
                                        setOk(true);}
                                    }
                                    className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg shadow-md text-xs md:text-sm transition-all"
                                >
                                    OK
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
                                    setTimeout(() => {
                                        if (stompClientRef.current) {
                                            stompClientRef.current.publish({
                                                destination: '/Game/Buy',
                                                body: JSON.stringify({
                                                    codeGame,
                                                    nickName: nickname,
                                                    buy: false,
                                                }),
                                            });
                                            setBuyPrompt(null);
                                        }
                                    }, 15000);
                                }else if(pendingNotifyPayPrompt && pieceName === pendingNotifyPayPrompt.pieceName){
                                    setNotifyPayPrompt({message: pendingNotifyPayPrompt.message})
                                    setPendingNotifyPayPrompt(null)
                                    setTimeout(() => {
                                        if (stompClientRef.current) {
                                            stompClientRef.current.publish({
                                                destination: '/Game/NextTurn',
                                                body: codeGame,
                                            });
                                            setOk(true);
                                            setNotifyPayPrompt(null);
                                        }
                                    }, 10000); 
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