import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { signInWithEmailAndPassword } from 'firebase/auth';
import { auth } from '../firebase';
import Cookies from 'js-cookie'
import axios from 'axios';

@Component({
  standalone: true,
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [CommonModule, FormsModule, RouterModule]
})
export class LoginComponent {
  usuario: string = '';
  contrasena: string = '';

  constructor(private router: Router) {}

  async login() {
    try {
      const userCredential = await signInWithEmailAndPassword(auth, this.usuario, this.contrasena);
      console.log('Usuario autenticado en Firebase:', userCredential.user);
      const user = {
        email: this.usuario,
        password: this.contrasena
      };
      const response = await axios.post('http://localhost:8001/User/Login', user);
      console.log("respuesta:", response.data.nickname);

      if (response.data.success) {
        Cookies.set('nickname', response.data.nickname, { expires: 7 });
        this.router.navigate(['/menu']);
      } else {
        console.log(response.data.error);
      }
    } catch (error) {
      console.error('Error al autenticar en Firebase:', error);
    }
  }
}
