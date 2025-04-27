// services/checkRoomCode.ts
import axios from 'axios';

export async function checkRoomCode(code: any): Promise<boolean> {
    try {
        const response = await axios.get(`http://localhost:8003/Game/Check`,code);
        return response.data.exists;
    } catch (error) {
        console.error('Error verificando el código de sala:', error);
        return false;
    }
}
