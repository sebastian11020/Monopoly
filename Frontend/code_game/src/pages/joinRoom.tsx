import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import InputCode from '../components/codeInput';
import axios from 'axios';

export default function JoinRoom() {
    const navigate = useNavigate();

    const handleJoinRoom = async (code: any) => {
        try {
            const response = await axios.get(`http://localhost:8003/Game/Check/${code}`);
            
            if (response.data.exists) {
                Cookies.set('roomCode', code);
                navigate(`/waiting-room`);
            } else {
                alert('Código inválido o sala no existente.');
            }
        } catch (error) {
            console.error('Error al verificar el código:', error);
            alert('Error al verificar el código.');
        }
    };

    return (
        <div
            className="min-h-screen flex items-center justify-center bg-cover bg-center"
            style={{ backgroundImage: "url('/Fichas/Fondo.jpg')" }}
        >
            <div className="bg-black bg-opacity-60 p-10 rounded-xl space-y-6 text-center">
                <h1 className="text-4xl font-extrabold text-yellow-300 mb-6">Unirse a una Sala</h1>
                <InputCode onSubmit={handleJoinRoom} />
            </div>
        </div>
    );
}
