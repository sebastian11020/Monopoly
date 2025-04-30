import { UserCircle } from 'lucide-react'; 

type PlayerCardProps = {
    name: string;
    token: string;
};

const PlayerCard = ({ name, token }: PlayerCardProps) => {
    return (
        <div className="flex items-center justify-between p-4 bg-white shadow-lg rounded-lg hover:shadow-2xl transition-shadow duration-300">
            <div className="flex items-center space-x-4 flex-1">
                {token ? (
                    <img
                        src={`/Fichas/${token}.png`}
                        alt={token}
                        className="w-12 h-12 object-contain"
                    />
                ) : (
                    <UserCircle size={48} className="text-gray-400" />
                )}
                <span className="text-base font-semibold text-gray-700">
                    {name}
                </span>
            </div>
            <span className="text-sm text-gray-500">En espera...</span>
        </div>
    );
};

export default PlayerCard;