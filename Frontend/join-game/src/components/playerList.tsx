
import PlayerCard from './playerCard';

const mockPlayers = [
    { name: 'Juan', token: 'Carro' },
    { name: 'Ana', token: 'Zapato' },
];

export default function PlayerList() {
    return (
        <div className="grid grid-cols-2 gap-4 mt-2">
            {mockPlayers.map((p, i) => (
                <PlayerCard key={i} name={p.name} token={p.token} />
            ))}
        </div>

    );
}
