import { Component, Inject, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BackendService } from '../backend.service';


interface Role {
  pkSecurityRoleId: string;
  roleName: string;
}
interface DialogData {
  firstName: string;
  secondName: string;
  password: string;
  email:string;
  username:string;
  role:string
}


@Component({
  selector: 'app-dialogbox',
  templateUrl: './dialogbox.component.html',
  styleUrls: ['./dialogbox.component.css']
})
export class DialogboxComponent implements OnInit {
  testModel: Role[];
  constructor( public dialogRef: MatDialogRef<DialogboxComponent>,
    @Inject(MAT_DIALOG_DATA) public data : DialogData,
    private backend:BackendService) { }

  ngOnInit(): void {
    this.getRolesvalue();
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
  
  roleName: string;
  pkSecurityRoleId : number;
  rolelist;
  getRolesvalue(){
    this.backend.getRole().subscribe(data=>{
      this.rolelist=data;
    })
  }
}
