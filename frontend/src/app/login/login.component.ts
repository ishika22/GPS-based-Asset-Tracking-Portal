import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BackendService } from '../backend.service';

@Component({
  selector: 'app-login',
  templateUrl: './login_component.html',
  styleUrls: ['./login_component.css']
})
export class LoginComponent implements OnInit {
  username:string
  password:string
  constructor(private router: Router,private backend:BackendService) { }
  
  login(){
    this.backend.autheticateUser(this.username,this.password).subscribe(
      isValid=>{
        if(isValid){
          this.router.navigate(['/home'])
        }
        else{
          alert('invalid credentials')
        }
      })
  }
  ngOnInit(): void {
  }

}
