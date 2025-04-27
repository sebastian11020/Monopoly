import Cookies from 'js-cookie';
import { Client } from '@stomp/stompjs';

type Player = {
    nickname: string;
    token: string;
};

type TokenSelectorProps = {
    players: Player[];
    roomCode: string;
    client: Client;
};

const TokenSelector = ({ players, roomCode, client }: TokenSelectorProps) => {
    const tokens = ['Carro', 'Barco', 'Sombrero', 'Zapato'];

    const selectedTokens = players.map(p => p.token).filter(token => token);

    const handleTokenClick = (token: string) => {
        const nickname = Cookies.get('nickname');

        if (!nickname) {
            console.error('No se encontró el nickname');
            return;
        }

        if (!client.connected) {
            console.error('Cliente WebSocket no está conectado');
            return;
        }

        const payload = {
            idGame: roomCode,
            nickName: nickname,
            namePiece: token,
        };

        console.log('Enviando mensaje al backend:', payload);

        try {
            client.publish({
                destination: '/Game/SelectPieceGame',
                body: JSON.stringify(payload),
            });
        } catch (error) {
            console.error('Error publicando selección de ficha:', error);
        }
    };


    return (
        <div className="space-x-4 flex flex-wrap justify-center">
            {tokens.map((token) => {
                const isTaken = selectedTokens.includes(token);
                return (
                    <div
                        key={token}
                        onClick={() => !isTaken && handleTokenClick(token)}
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
