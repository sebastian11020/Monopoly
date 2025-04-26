import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { createUserWithEmailAndPassword } from 'firebase/auth';
import { auth } from '../firebase'; // ajusta la ruta según esté tu archivo
import axios from 'axios'

@Component({
  standalone: true,
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  imports: [CommonModule, FormsModule, RouterModule]
})

export class RegisterComponent {
  correo: string = '';
  usuario: string = '';
  contrasena: string = '';

  async registrar() {
    const user ={
      email: this.correo,
      nickname: this.usuario,
      password : this.contrasena
    }
    try{
      const userCredential = await createUserWithEmailAndPassword(auth,user.email, user.password)
      console.log('Usuario creado en Firebase:', userCredential.user);
      const response = await axios.post('http://localhost:8001/User/Create', user)
      if (response.data.success==="true"){
        console.log(response.data.confirm)
      }else {
        console.log(response.data.error)
      }
    }catch (error){
      console.error(error)
    }
  }
}
