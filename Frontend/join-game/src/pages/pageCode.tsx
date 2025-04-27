import { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Para manejar la redirección
import axios from 'axios';
import Cookies from 'js-cookie';

const JoinGamePage = () => {
    const [gameCode, setGameCode] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const history = useNavigate(); // Hook de redirección

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const gameCodeSumbit = {
            gameCode:gameCode
        }
        try {
            console.log('Intentando unirse a la partida:', gameCode);
            const response = await axios.post('http://localhost:8003/Game/Check',gameCodeSumbit);
            if (response.data.success) {
                // Si la partida existe, guardamos el código en las cookies
                Cookies.set('gameCode', gameCode); // Guardamos como cadena en las cookies

                // Redirigimos al usuario a la página de la sala de espera
                history('/waiting-room');
            } else {
                // Si la partida no existe, mostramos un mensaje de error
                setErrorMessage('El código de la partida no es válido.');
            }
        } catch (error) {
            console.error('Error al verificar el código de la partida:', error);
            setErrorMessage('Hubo un error al intentar unirse a la partida.');
        }
    };

    return (
        <div className="min-h-screen flex justify-center items-center bg-gray-800 text-white">
            <div className="bg-black bg-opacity-70 p-6 rounded-lg shadow-lg">
                <h2 className="text-2xl font-bold mb-4">Unirse a la partida</h2>
                <form onSubmit={handleSubmit} className="flex flex-col space-y-4">
                    <input
                        type="text"
                        placeholder="Código de la partida"
                        value={gameCode}
                        onChange={(e) => setGameCode(e.target.value)}
                        className="px-4 py-2 rounded bg-gray-900 text-white"
                        required
                    />
                    {errorMessage && <p className="text-red-500">{errorMessage}</p>}
                    <button
                        type="submit"
                        className="px-6 py-2 bg-blue-600 hover:bg-blue-700 rounded text-white"
                    >
                        Unirse a la partida
                    </button>
                </form>
            </div>
        </div>
    );
};

export default JoinGamePage;

