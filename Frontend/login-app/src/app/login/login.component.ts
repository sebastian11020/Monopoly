import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms'; // <--- importa esto
import { CommonModule } from '@angular/common'; // para *ngIf, *ngFor, etc.
import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [CommonModule, FormsModule, RouterModule] // <--- importa aquí
})
export class LoginComponent {
  usuario: string = '';
  contrasena: string = '';

  login() {
    console.log('Usuario:', this.usuario);
    console.log('Contraseña:', this.contrasena);
  }
}
