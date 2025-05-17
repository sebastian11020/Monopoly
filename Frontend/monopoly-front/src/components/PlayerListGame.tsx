import { Player } from '../utils/type';

interface PlayerListProps {
    players: Player[];
    onSelect: (player: Player) => void;
}

const PlayerList = ({ players, onSelect }: PlayerListProps) => (
    <div className="flex justify-around items-center py-2 bg-black/80 text-sm font-semibold shadow-md">
        {players.map((j, index) => (
            <div
                key={index}
                onClick={() => onSelect(j)}
                className="flex items-center gap-2 bg-white/10 px-3 py-1 rounded-lg shadow-sm cursor-pointer hover:bg-white/20 transition"
            >
                <img src={`/Fichas/${j.piece.name}.png`} alt="icono" className="w-8 h-8" />
                <span>{j.nickName}</span>
                <span className="text-green-300">${j.cash}</span>
                <span className={j.turn.active ? 'text-yellow-400' : 'text-gray-400'}>
                    {j.turn.active ? '🎲 Su turno' : 'Esperando'}
                </span>
                <span className="text-xs">({j.dice1} + {j.dice2})</span>
            </div>
        ))}
    </div>
);

export default PlayerList;
