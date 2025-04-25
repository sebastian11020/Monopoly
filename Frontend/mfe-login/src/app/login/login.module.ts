import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './login.component'; // debe ser standalone también
import { LoginFormComponent } from './login-form/login-form.component';

@NgModule({
  imports: [
    CommonModule,
    LoginComponent,
    LoginFormComponent
  ]
})
export class LoginModule {}
