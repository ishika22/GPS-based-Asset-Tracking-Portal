import { Component, Inject, OnInit } from '@angular/core';
import {  MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-message-dialog-box',
  templateUrl: './message-dialog-box.component.html',
  styleUrls: ['./message-dialog-box.component.css']
})
export class MessageDialogBoxComponent implements OnInit {

  constructor( public dialogRef: MatDialogRef<MessageDialogBoxComponent>,@Inject(MAT_DIALOG_DATA) public data: any) {}
    onNoClick(): void {
        this.dialogRef.close();
      }

  ngOnInit(): void {
  }

}
