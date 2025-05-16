import MoneyDisplay from './MoneyDisplay';
import ActionButtons from './ActionButtons';
import { Player } from '../utils/type';

interface SidebarProps {
    currentPlayer: Player | null;
}

const Sidebar = ({ currentPlayer }: SidebarProps) => (
    <div className="flex flex-col justify-between w-1/3 p-4 bg-black/70 rounded-tl-3xl shadow-inner border-l border-white/10">
        <div>
            <h3 className="text-xl font-bold mb-2 text-yellow-300">Tus propiedades</h3>
            <div className="flex flex-wrap gap-3 mb-4">
                <span className="text-gray-400 italic">No implementado aún</span>
            </div>
        </div>

        <div className="px-4 py-3 flex flex-col gap-3 shadow-inner border-t border-white/10 rounded-lg">
            <div className="flex items-center gap-3 rounded-md p-3 shadow-lg">
                {currentPlayer && (
                    <>
                        <img src={`/Fichas/${currentPlayer.piece.name}.png`} className="w-10 h-10" alt="ficha" />
                        <p className="text-lg font-bold text-yellow-400">{currentPlayer.nickName}</p>
                    </>
                )}
            </div>

            <MoneyDisplay cash={currentPlayer?.cash ?? 0} />
            <ActionButtons />
        </div>
    </div>
);

export default Sidebar;
