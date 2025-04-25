import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginFormComponent } from './login-form/login-form.component';
import { LoginErrorComponent } from './login-error/login-error.component';
import { LoginLoadingComponent } from './login-loading/login-loading.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    LoginFormComponent,
    LoginErrorComponent,
    LoginLoadingComponent
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  errorMessage: string | null = null;
  isLoading = false;
}
