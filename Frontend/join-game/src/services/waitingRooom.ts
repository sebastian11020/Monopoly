import axios from 'axios';

export default async function waitingRoomCode() {
    try {
        const response = await axios.get('http://localhost:8003/Game/Create');
        return response.data.codeGame;
    } catch (error) {
        console.error(error);
    }
}
