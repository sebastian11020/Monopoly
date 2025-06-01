import axios from 'axios';

const API_URL = import.meta.env.VITE_API_URL;

export async function waitingRoomCode(nickname: any) {
    try {
        const response = await axios.post(`${API_URL}/Game/Create`, nickname);
        return response.data.codeGame;
    } catch (error) {
        console.error(error);
    }
}

export async function waitingRoomExit(nickName: any, codeGame: any) {
    const exitGame = {
        nickName: nickName,
        codeGame: parseInt(codeGame),
    };
    try {
        const response = await axios.post(`${API_URL}/Game/Exit`, exitGame);
        return response.data;
    } catch (error) {
        console.error(error);
    }
}
