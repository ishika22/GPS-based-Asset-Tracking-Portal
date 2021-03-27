import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-login',
  templateUrl: './login_component.html',
  styleUrls: ['./login_component.css']
})
export class LoginComponent implements OnInit {

  constructor() { }
  
  login(){
    console.log('login logic');
  }
  ngOnInit(): void {
  }

}
