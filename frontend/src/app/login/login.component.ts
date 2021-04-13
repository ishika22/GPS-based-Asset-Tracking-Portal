import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BackendService } from '../backend.service';
import { MessageDialogBoxComponent} from '../message-dialog-box/message-dialog-box.component';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-login',
  templateUrl: './login_component.html',
  styleUrls: ['./login_component.css']
})
export class LoginComponent implements OnInit {
  username:string
  password:string
  constructor(private router: Router,private backend:BackendService,public dialog: MatDialog) { 
    history.pushState(null, null, window.location.href);  
  history.pushState(null, null, window.location.href);
  }
  
  login(){
    this.backend.autheticateUser(this.username,this.password).subscribe(
      token=>{
          localStorage.setItem('token', token.token);
          this.router.navigate(['/home'])
      },
      err=>{let payload = {};
      payload['title'] = 'Login user';
      payload['body'] = 'Either username or password is incorrect';
      this.dialog.open(MessageDialogBoxComponent,{data:payload});})
  }
  ngOnInit(): void {
  }

}
