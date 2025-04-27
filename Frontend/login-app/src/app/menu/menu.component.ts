import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css'],
  imports: [CommonModule, RouterModule]
})
export class MenuComponent {
  mostrarSubmenuJugar = false;

  notificacion: { tipo: 'exito' | 'error'; mensaje: string } | null = null;

  toggleSubmenu() {
    this.mostrarSubmenuJugar = !this.mostrarSubmenuJugar;
  }

  waitRoom() {
    this.mostrarNotificacion('exito', 'Redirigiendo a sala de espera...');
    setTimeout(() => {
      window.location.href = "http://localhost:5173/waiting-room";
    }, 1500);
  }
  joinRoom(){
    this.mostrarNotificacion('exito', 'Redirigiendo a sala de espera...');
    setTimeout(() => {
      window.location.href = "http://localhost:5173/page-code";
    }, 1500);
  }
  mostrarNotificacion(tipo: 'exito' | 'error', mensaje: string) {
    this.notificacion = { tipo, mensaje };
    setTimeout(() => {
      this.notificacion = null;
    }, 3000);
  }
}
