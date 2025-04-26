import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { signInWithEmailAndPassword } from 'firebase/auth';
import { auth } from '../firebase';

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
      console.log("Usuario logueado", userCredential.user);

      // Redirigir al menú
      this.router.navigate(['/menu']);
    } catch (error) {
      console.error(error);
    }
  }
}
