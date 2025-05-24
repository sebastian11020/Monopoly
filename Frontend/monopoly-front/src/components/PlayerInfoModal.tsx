import { useState } from 'react';
import { Player } from '../utils/type';

interface Props {
    jugador: Player;
    onClose: () => void;
}

const PlayerInfoModal = ({ jugador, onClose }: Props) => {
    const [selectedProperty, setSelectedProperty] = useState<string | null>(null);

    return (
        <div className="fixed inset-0 bg-black bg-opacity-80 flex justify-center items-center z-50 font-['Press_Start_2P']">
            <div className="bg-gradient-to-br from-zinc-900 via-black to-zinc-800 border-4 border-yellow-400 rounded-2xl p-6 w-[420px] shadow-[0_0_40px_rgba(255,255,0,0.5)] relative">
                {/* Botón cerrar principal */}
                <button
                    onClick={onClose}
                    className="absolute top-3 right-3 text-sm bg-red-600 hover:bg-red-700 text-white px-3 py-1 rounded-full shadow-md z-10"
                >
                    ✕
                </button>

                {/* Información del jugador */}
                <div className="flex items-center gap-4 mb-5">
                    <img
                        src={`/Fichas/${jugador.piece.name}.png`}
                        alt={jugador.piece.name}
                        className="w-12 h-12 object-contain drop-shadow-[0_0_6px_rgba(255,255,255,0.6)]"
                    />
                    <div>
                        <h2 className="text-xl text-yellow-300">{jugador.nickName}</h2>
                        <p className="text-green-300 mt-1 text-sm">💰 ${jugador.cash}</p>
                        <p className="text-white text-sm">🎲 {jugador.dice1} + {jugador.dice2}</p>
                    </div>
                </div>

                {/* Propiedades */}
                {
                    // @ts-ignore
                    jugador.cards?.length > 0 && (
                    <div className="mb-6">
                        <h3 className="text-sm text-yellow-300 mb-2">Propiedades:</h3>
                        <div className="flex flex-wrap gap-2 bg-white/10 p-2 rounded-md">
                            {jugador.cards?.map((card, idx: number) => (
                                <img
                                    key={idx}
                                    src={`/assets/${card.name}.png`}
                                    alt={card.name}
                                    onClick={() => setSelectedProperty(card.name)}
                                    className="w-16 h-auto rounded shadow cursor-pointer hover:scale-110 transition-transform"
                                />
                            ))}
                        </div>
                    </div>
                )}

                {/* Acciones del jugador */}
                <div className="flex justify-between gap-3">
                    <button className="flex-1 bg-blue-600 hover:bg-blue-700 text-white text-xs py-2 rounded-lg shadow-md">
                        💬 Mensaje
                    </button>
                    <button className="flex-1 bg-green-600 hover:bg-green-700 text-white text-xs py-2 rounded-lg shadow-md">
                        🤝 Hacer trato
                    </button>
                </div>
            </div>

            {/* Modal de propiedad ampliada */}
            {selectedProperty && (
                <div
                    className="fixed inset-0 bg-black bg-opacity-70 flex justify-center items-center z-50"
                    onClick={() => setSelectedProperty(null)}
                >
                    <div className="relative">
                        <img
                            src={`/assets/${selectedProperty}.png`}
                            alt={selectedProperty}
                            className="max-w-[90vw] max-h-[90vh] rounded-lg shadow-lg border-4 border-yellow-400"
                        />
                        <button
                            onClick={() => setSelectedProperty(null)}
                            className="absolute top-2 right-2 bg-red-600 hover:bg-red-700 text-white px-2 py-1 rounded-full text-xs shadow-md"
                        >
                            ✕
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default PlayerInfoModal;
