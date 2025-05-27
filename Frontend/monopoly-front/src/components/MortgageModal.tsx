interface MortgageModalProps {
    properties: { id:number, name: string }[];
    onClose: () => void;
    onSelect: (propertyId: number) => void;
}

const MortgageModal = ({ properties, onClose, onSelect }: MortgageModalProps) => {
    return (
        <div className="fixed inset-0 z-50 bg-black/70 flex items-center justify-center">
            <div
                onClick={onClose}
                className="absolute inset-0"
            ></div>

            <div className="relative z-50 bg-gray-900 border border-yellow-300 p-6 rounded-2xl max-w-2xl w-full shadow-2xl text-white">
                <h2 className="text-2xl font-bold mb-4 text-yellow-300">Selecciona una propiedad para hipotecar</h2>
                <div className="flex flex-wrap gap-4 justify-center max-h-[60vh] overflow-y-auto">
                    {properties.map((card, idx) => (
                        <div
                            key={idx}
                            onClick={() => onSelect(card.id)}
                            className="cursor-pointer hover:scale-110 transition-transform"
                        >
                            <img
                                src={`/assets/${card.name}.png`}
                                alt={card.name}
                                className="w-24 h-auto rounded shadow border border-white/20"
                            />
                        </div>
                    ))}
                </div>
                <button
                    onClick={onClose}
                    className="absolute top-3 right-3 bg-red-500 hover:bg-red-600 rounded-full w-8 h-8 flex items-center justify-center text-lg font-bold"
                >
                    ✖
                </button>
            </div>
        </div>
    );
};

export default MortgageModal;
