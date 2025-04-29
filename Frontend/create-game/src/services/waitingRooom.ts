import axios from 'axios';

export async function waitingRoomCode(nickname:any) {
    try {
        const response = await axios.post('http://localhost:8003/Game/Create',nickname);
        return response.data.codeGame;
    } catch (error) {
        console.error(error);
    }
}

export async function waitingRoomExit(nickName:any,codeGame:any) {
    const exitGame = {
        nickName:nickName,
        codeGame:parseInt(codeGame),
    }
    try{
        const response = await axios.post('http://localhost:8003/Game/Exit',exitGame);
        return response.data;
    }catch (error){
        console.error(error);
    }
}
