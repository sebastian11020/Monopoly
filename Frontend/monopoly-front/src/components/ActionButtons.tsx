const ActionButtons = ({ onMortgageClick, onBuildClick }: { onMortgageClick: () => void; onBuildClick: () => void }) => (
    <div className="flex gap-3">
        <button
            className="bg-yellow-400 hover:bg-yellow-500 text-black font-bold px-4 py-2 rounded shadow-md"
            onClick={onMortgageClick}
        >
            Hipotecar
        </button>
        <button
            className="bg-blue-500 hover:bg-blue-600 text-white font-bold px-4 py-2 rounded shadow-md"
            onClick={onBuildClick}
        >
            Construir
        </button>
    </div>
);


export default ActionButtons;
