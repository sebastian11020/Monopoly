import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { signInWithEmailAndPassword } from 'firebase/auth';
import { auth } from '../firebase';
import axios from  'axios';

@Component({
  standalone: true,
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [CommonModule, FormsModule, RouterModule, HttpClientModule]
})
export class LoginComponent {
  usuario: string = '';
  contrasena: string = '';

  constructor(private router: Router, private http: HttpClient) {}

  async login() {
    try {
      const userCredential = await signInWithEmailAndPassword(auth, this.usuario, this.contrasena);
      console.log('Usuario autenticado en Firebase:', userCredential.user);

      const user ={
        email: this.usuario,
        password : this.contrasena
      }

      const response = await axios.post('http://localhost:8080/User/Login' ,user);
      if (response.data.success) {
        localStorage.setItem('nickname', response.data.nickname);
      }else {
        console.log(response.data.error);
      }
    } catch (error) {
      console.error('Error al autenticar en Firebase:', error);
    }
  }

}
