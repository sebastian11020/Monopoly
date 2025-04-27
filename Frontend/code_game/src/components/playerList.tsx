import PlayerCard from './playerCard';

interface Player {
    nickname: string;
    token: string;
}

interface PlayerListProps {
    players: Player[];
}

export default function PlayerList({ players }: PlayerListProps) {
    return (
        <div className="grid grid-cols-2 gap-4 mt-2">
            {players.length > 0 ? (
                players.map((p, i) => (
                    <PlayerCard key={i} name={p.nickname} token={p.token} />
                ))
            ) : (
                <p>No hay jugadores aún</p> // Mensaje de depuración si no hay jugadores
            )}
        </div>
    );
}

