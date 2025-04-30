import { UserCircle } from 'lucide-react';

type PlayerCardProps = {
    name: string;
    token: string;
    state: boolean;
};

const PlayerCard = ({ name, token, state }: PlayerCardProps) => {
    const statusText = state ? '¡Preparado!' : 'En espera...';
    const statusColor = state ? 'text-green-500' : 'text-yellow-500';
    const bgColor = state ? 'bg-green-50' : 'bg-yellow-50';

    return (
        <div className={`flex items-center justify-between px-6 py-4 ${bgColor} rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300`}>
            <div className="flex items-center space-x-5">
                {token ? (
                    <img
                        src={`/Fichas/${token}.png`}
                        alt={token}
                        className="w-14 h-14 object-contain"
                    />
                ) : (
                    <UserCircle size={56} className="text-gray-400" />
                )}
                <div>
                    <p className="text-lg font-bold text-gray-800">{name}</p>
                    <p className={`text-sm font-medium ${statusColor}`}>{statusText}</p>
                </div>
            </div>
        </div>
    );
};

export default PlayerCard;
