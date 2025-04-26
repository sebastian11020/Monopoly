import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { signInWithEmailAndPassword } from 'firebase/auth';
import { auth } from '../firebase';

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
      // Primero login en Firebase
      const userCredential = await signInWithEmailAndPassword(auth, this.usuario, this.contrasena);
      console.log('Usuario autenticado en Firebase:', userCredential.user);
 
      const email = userCredential.user.email;
      const password = this.contrasena; 
  
      const body = { email, password };
  
      this.http.post<{ success: boolean, data: { nickname: string } }>('https://3000/login', body)
        .subscribe({
          next: (response) => {
            if (response.success) {
              console.log('Nickname recibido:', response.data.nickname);
  
              // Guardamos el nickname en localStorage
              localStorage.setItem('nickname', response.data.nickname);
  
              // Redirigimos al menú
              this.router.navigate(['/menu']);
            } else {
              console.error('Backend respondió: login fallido');
            }
          },
          error: (error) => {
            console.error('Error en la comunicación con el backend', error);
          }
        });
  
    } catch (error) {
      console.error('Error al autenticar en Firebase:', error);
    }
  }
  
}
