import { Player } from '../utils/type';

interface Props {
    jugador: Player;
    onClose: () => void;
}

const PlayerInfoModal = ({ jugador, onClose }: Props) => {
    return (
        <div className="fixed inset-0 bg-black bg-opacity-80 flex justify-center items-center z-50 font-['Press_Start_2P']">
            <div className="bg-gradient-to-br from-zinc-900 via-black to-zinc-800 border-4 border-yellow-400 rounded-2xl p-6 w-[350px] shadow-[0_0_40px_rgba(255,255,0,0.5)]">
                <div className="flex justify-between items-center mb-4">
                    <div className="flex items-center gap-3">
                        <img
                            src={`/Fichas/${jugador.piece.name}.png`}
                            alt={jugador.piece.name}
                            className="w-10 h-10 object-contain drop-shadow-[0_0_6px_rgba(255,255,255,0.6)]"
                        />
                        <h2 className="text-lg text-yellow-300 truncate max-w-[180px]">
                            {jugador.nickName}
                        </h2>
                    </div>
                    <button
                        onClick={onClose}
                        className="text-sm bg-red-600 hover:bg-red-700 text-white px-3 py-1 rounded-full shadow-md"
                    >
                        ✕
                    </button>
                </div>

                <ul className="space-y-2 text-sm text-white">
                    <li className="text-green-300 font-bold">💰 Dinero: ${jugador.cash}</li>
                    <li>🎲 Dados: {jugador.dice1} + {jugador.dice2}</li>
                </ul>

                <div className="mt-6 flex justify-between gap-3">
                    <button className="flex-1 bg-blue-600 hover:bg-blue-700 text-white text-xs py-2 rounded-lg shadow-md">
                        💬 Mensaje
                    </button>
                    <button className="flex-1 bg-green-600 hover:bg-green-700 text-white text-xs py-2 rounded-lg shadow-md">
                        🤝 Hacer trato
                    </button>
                </div>
            </div>
        </div>
    );
};

export default PlayerInfoModal;
