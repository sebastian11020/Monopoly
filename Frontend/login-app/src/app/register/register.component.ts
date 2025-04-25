import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

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

  registrar() {
    console.log('Correo:', this.correo);
    console.log('Usuario:', this.usuario);
    console.log('Contraseña:', this.contrasena);
  }
}
