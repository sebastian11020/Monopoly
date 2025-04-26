import Header from '../components/header';
import GameCode from '../components/gameCode';
import PlayerList from '../components/playerList';
import TokenSelector from '../components/TokenSelector';

export default function WaitingRoom() {
    const handleStartGame = () => {
        console.log("¡La partida comienza!");
        // Aquí puedes redirigir a la pantalla del juego o iniciar la lógica
    };

    return (
        <div
            className="min-h-screen bg-cover bg-center text-white"
            style={{ backgroundImage: "url('/Fichas/Fondo.jpg')" }} // Asegúrate de que esté en /public/Fichas/
        >
            <div className="bg-black bg-opacity-50 min-h-screen flex flex-col items-center justify-center py-16 space-y-10 px-4">
                <Header />
                <GameCode code="123456" />
                <PlayerList />
                <TokenSelector />

                {/* Botón de empezar partida */}
                <button
                    onClick={handleStartGame}
                    className="mt-6 px-8 py-3 bg-green-500 hover:bg-green-600 text-white text-lg font-bold rounded-full shadow-lg transition-all duration-300"
                >
                    Empezar partida
                </button>
            </div>
        </div>
    );
}
