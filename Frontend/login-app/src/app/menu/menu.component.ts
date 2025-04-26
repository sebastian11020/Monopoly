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

  toggleSubmenu() {
    this.mostrarSubmenuJugar = !this.mostrarSubmenuJugar;
  }
}
