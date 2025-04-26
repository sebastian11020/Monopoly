import io from 'socket.io-client';

const socket = io('http://localhost:3000');

type Player = {
    nickname: string;
    token: string;
};

type TokenSelectorProps = {
    players: Player[];
    roomCode: string;
};

const TokenSelector = ({ players, roomCode }: TokenSelectorProps) => {
    const tokens = ['Carro', 'Barco', 'Sombrero', 'Zapato'];

    const selectedTokens = players.map(p => p.token).filter(token => token);

    const handleTokenClick = (token: string) => {
        const nickname = localStorage.getItem('nickname');
        if (nickname) {
            socket.emit('select-token', { nickname, roomCode, token });
        }
    };

    return (
        <div className="space-x-4 flex flex-wrap justify-center">
            {tokens.map((token) => {
                const isTaken = selectedTokens.includes(token);
                return (
                    <div
                        key={token}
                        onClick={() => !isTaken && handleTokenClick(token)} // Solo puedes hacer click si NO está ocupado
                        className={`cursor-pointer transform transition-all duration-200 rounded-full p-4 shadow-md
                            ${isTaken ? 'opacity-40 cursor-not-allowed' : 'bg-white hover:scale-110 hover:shadow-2xl'}`}
                    >
                        <img
                            src={`/Fichas/${token}.png`}
                            alt={token}
                            className="w-10 h-10 object-contain"
                        />
                    </div>
                );
            })}
        </div>
    );
};

export default TokenSelector;
