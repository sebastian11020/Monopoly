import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login-error',
  standalone: true,
  imports: [CommonModule],
  template: `<div class="text-red-600 mt-2" *ngIf="errorMessage">{{ errorMessage }}</div>`
})
export class LoginErrorComponent {
  @Input() errorMessage: string | null = null;
}
