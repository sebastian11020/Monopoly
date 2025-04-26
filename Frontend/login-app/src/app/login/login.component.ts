import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
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

  async login() {
    try {
      const userCredential = await signInWithEmailAndPassword(auth, this.usuario, this.contrasena);
      console.log("Usuario logueado",userCredential.user);
    }catch (error) {
      console.error(error);
    }
  }
}
