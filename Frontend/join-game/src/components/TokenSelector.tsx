// src/components/TokenSelector.tsx
const TokenSelector = () => {
    const tokens = ['Carro', 'Barco', 'Sombrero', 'Zapato'];

    return (
        <div className="space-x-4 flex">
            {tokens.map((token) => (
                <div
                    key={token}
                    className="cursor-pointer transform transition-all duration-200 hover:scale-110 hover:shadow-2xl rounded-full p-4 bg-white shadow-md"
                >
                    <img
                        src={`/Fichas/${token}.png`}  // Ruta directa desde public/
                        alt={token}
                        className="w-10 h-10 object-contain"
                    />
                </div>
            ))}
        </div>
    );
};

export default TokenSelector;
