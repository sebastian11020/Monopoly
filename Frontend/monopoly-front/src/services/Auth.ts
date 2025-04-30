import axios from "axios";

export  async function Register (email:any,nickName:any,password:any) {
    const user = {
        email:email,
        nickName:nickName,
        password:password,
    }
    try {
        const response = await axios.post('http://localhost:8001/User/Create',user);
        console.log("Respuesta: ",response.data);
        return response.data;
    }catch (error){
        console.error(error);
    }
}

export async function Login (email:any,password:any) {
    const user = {
        email:email,
        password:password,
    }
    try {
        const response = await axios.post('http://localhost:8001/User/Login',user);
        console.log("Respuesta: ",response.data);
        return response.data;
    }catch (error){
        console.error(error);
    }
}