import axios from "axios";

const API_URL = import.meta.env.VITE_API_URL;

export async function Register(email: any, nickName: any, password: any) {
    const user = {
        email: email,
        nickname: nickName,
        password: password,
    };
    try {
        const response = await axios.post(`${API_URL}/User/Create`, user);
        console.log("Respuesta: ", response.data);
        return response.data;
    } catch (error) {
        console.error(error);
    }
}

export async function Login(email: any, password: any) {
    const user = {
        email: email,
        password: password,
    };
    try {
        const response = await axios.post(`${API_URL}/User/Login`, user);
        console.log("Respuesta: ", response.data);
        return response.data;
    } catch (error) {
        console.error(error);
    }
}
