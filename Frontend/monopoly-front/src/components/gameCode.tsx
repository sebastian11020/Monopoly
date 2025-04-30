type GameCodeProps = {
    code: string;
};

export default function GameCode({ code }: GameCodeProps) {
    const handleCopy = async () => {
        try {
            await navigator.clipboard.writeText(code);
            console.log('Código copiado:', code);
        } catch (error) {
            console.error('Error al copiar el código:', error);
        }
    };

    return (
        <div className="flex justify-between items-center bg-white border-2 border-green-600 p-4 rounded-xl shadow font-semibold text-lg gap-x-4">
            <span className="text-gray-800">Código de sala: {code}</span>
            <button
                onClick={handleCopy}
                className="bg-green-500 text-white px-4 py-1.5 rounded hover:bg-green-600 shadow transition-all"
            >
                Copiar
            </button>
        </div>
    );
}