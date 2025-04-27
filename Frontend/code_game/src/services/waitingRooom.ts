import axios from 'axios';

export default async function waitingRoomCode(nickname:any) {
    try {
        const response = await axios.post('http://localhost:8003/Game/Create',nickname);
        return response.data.codeGame;
    } catch (error) {
        console.error(error);
    }
}
