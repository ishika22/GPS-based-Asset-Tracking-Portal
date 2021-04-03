import { Component, Inject, Injectable } from '@angular/core';
import { AngularFireMessaging } from '@angular/fire/messaging';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BehaviorSubject } from 'rxjs'
@Injectable()
export class MessagingService {
currentMessage = new BehaviorSubject(null);
constructor(private angularFireMessaging: AngularFireMessaging,
    public dialog: MatDialog) {

}
requestPermission() {
this.angularFireMessaging.requestToken.subscribe(
(token) => {
console.log(token);
localStorage.setItem('FCMToken',token)
},
(err) => {
console.error('Unable to get permission to notify.', err);
localStorage.setItem('FCMToken',null)
}
);
}
receiveMessage() {
this.angularFireMessaging.messages.subscribe(
(payload) => {
console.log("new message received. ", payload);
this.dialog.open(DialogNotification,{data:payload})
this.currentMessage.next(payload);
})
}
}
@Component({
    selector: 'dialog-notify',
    templateUrl: 'dialog.html',
  })
  export class DialogNotification {
    constructor( public dialogRef: MatDialogRef<DialogNotification>,@Inject(MAT_DIALOG_DATA) public data: any) {}
    onNoClick(): void {
        this.dialogRef.close();
      }
  }