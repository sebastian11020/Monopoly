import { useState } from 'react';
import MoneyDisplay from './MoneyDisplay';
import ActionButtons from './ActionButtons';
import { Player } from '../utils/type';

interface SidebarProps {
    currentPlayer: Player | null;
}

const Sidebar = ({ currentPlayer }: SidebarProps) => {
    const [selectedCard, setSelectedCard] = useState<string | null>(null);

    if (!currentPlayer) return null;

    return (
        <>
            <div className="flex flex-col justify-between w-1/3 p-4 bg-black/70 rounded-tl-3xl shadow-inner border-l border-white/10">
                <div>
                    {currentPlayer.namesCards?.length > 0 && (
                        <>
                            <h3 className="text-xl font-bold mb-2 text-yellow-300">Tus propiedades</h3>
                            <div className="flex flex-wrap gap-2 bg-white/10 p-2 rounded-md">
                                {currentPlayer.namesCards.map((cardName: string, idx: number) => (
                                    <img
                                        key={idx}
                                        src={`/assets/${cardName}.png`}
                                        alt={cardName}
                                        onClick={() => setSelectedCard(cardName)}
                                        className="w-16 h-auto rounded-md shadow-md transition-transform duration-300 hover:scale-125 cursor-pointer"
                                    />
                                ))}
                            </div>
                        </>
                    )}
                </div>

                <div className="px-4 py-3 flex flex-col gap-3 shadow-inner border-t border-white/10 rounded-lg">
                    <div className="flex items-center gap-3 rounded-md p-3 shadow-lg">
                        <img src={`/Fichas/${currentPlayer.piece.name}.png`} className="w-10 h-10" alt="ficha" />
                        <p className="text-lg font-bold text-yellow-400">{currentPlayer.nickName}</p>
                    </div>

                    <MoneyDisplay cash={currentPlayer.cash} />
                    <ActionButtons />
                </div>
            </div>

            {/* Modal de imagen ampliada */}
            {selectedCard && (
                <div
                    onClick={() => setSelectedCard(null)}
                    className="fixed inset-0 z-50 bg-black bg-opacity-70 flex items-center justify-center"
                >
                    {/* Contenedor evita que el clic en la imagen cierre el modal */}
                    <div
                        onClick={(e) => e.stopPropagation()}
                        className="relative bg-white/10 p-4 rounded-xl shadow-2xl"
                    >
                        <button
                            onClick={() => setSelectedCard(null)}
                            className="absolute top-2 right-2 text-white bg-red-500 hover:bg-red-600 rounded-full w-8 h-8 flex items-center justify-center font-bold text-lg shadow"
                        >
                            ✖
                        </button>
                        <img
                            src={`/assets/${selectedCard}.png`}
                            alt={selectedCard}
                            className="max-w-[90vw] max-h-[80vh] rounded-lg border-4 border-yellow-300"
                        />
                    </div>
                </div>
            )}
        </>
    );
};

export default Sidebar;
