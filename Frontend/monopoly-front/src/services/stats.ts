import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL;

export async function Stats(nickName: any) {
    try {
        const response = await axios.post(`${API_URL}/Stats/player/${nickName}`);
        console.log("Respuesta: ", response.data);
        return response.data;
    } catch (error) {
        console.error(error);
    }
}