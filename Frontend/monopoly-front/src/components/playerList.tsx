import PlayerCard from './playerCard';

interface Player {
    nickname: string;
    token: string;
    state: boolean;
}

interface PlayerListProps {
    players: Player[];
}

export default function PlayerList({ players }: PlayerListProps) {
    return (
        <div className="grid grid-cols-2 gap-4 mt-2">
            {players.length > 0 ? (
                players.map((p, i) => (
                    <PlayerCard key={i} name={p.nickname} token={p.token} state={p.state} />
                ))
            ) : (
                <p>No hay jugadores aún</p> 
            )}
        </div>
    );
}

