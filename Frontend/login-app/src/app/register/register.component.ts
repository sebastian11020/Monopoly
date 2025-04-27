import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { createUserWithEmailAndPassword } from 'firebase/auth';
import { auth } from '../firebase';
import axios from 'axios';

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

  notificacion: { tipo: 'exito' | 'error'; mensaje: string } | null = null;

  constructor(private router: Router) {}

  async registrar() {
    const user = {
      email: this.correo,
      nickname: this.usuario,
      password: this.contrasena
    };

    try {
      const userCredential = await createUserWithEmailAndPassword(auth, user.email, user.password);
      console.log('Usuario creado en Firebase:', userCredential.user);

      const response = await axios.post('http://localhost:8001/User/Create', user);

      if (response.data.success === true) {
        console.log(response.data.confirm);
        this.mostrarNotificacion('exito', '¡Registro exitoso!');
        setTimeout(() => this.router.navigate(['/']), 2000); 
      } else {
        console.log(response.data.error);
        this.mostrarNotificacion('error', 'Registro fallido. Intenta de nuevo.');
      }
    } catch (error) {
      console.error(error);
      this.mostrarNotificacion('error', 'Registro fallido. Intenta de nuevo.');
    }
  }

  mostrarNotificacion(tipo: 'exito' | 'error', mensaje: string) {
    this.notificacion = { tipo, mensaje };
    setTimeout(() => {
      this.notificacion = null;
    }, 3000); 
  }
}