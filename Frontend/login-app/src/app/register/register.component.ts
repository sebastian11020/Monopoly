import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms'; // <--- importa esto
import { CommonModule } from '@angular/common'; // para *ngIf, *ngFor, etc.

@Component({
  standalone: true,
  selector: 'app-login',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  imports: [CommonModule, FormsModule] // <--- importa aquí
})

export class RegisterComponent {
  usuario: string = '';
  contrasena: string = '';
  correo: string = '';

  login() {
    console.log('Usuario:', this.usuario);
    console.log('Contraseña:', this.contrasena);
    console.log('Correo:', this.correo);
  }
}
