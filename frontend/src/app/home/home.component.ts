import { ChangeDetectorRef, Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AssetDetail } from '../AssetDetail';
import { BackendService } from '../backend.service';
import { DataBindingService } from '../data-binding.service';
import { MapsComponent } from '../maps/maps.component';
import {MediaMatcher} from '@angular/cdk/layout';
import { DialogboxComponent} from '../dialogbox/dialogbox.component';
import { MessageDialogBoxComponent} from '../message-dialog-box/message-dialog-box.component';
import { BehaviorSubject } from 'rxjs'

interface Types {
  value: string;
  viewValue: string;
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
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  currentMessage = new BehaviorSubject(null);

  @ViewChild(MapsComponent, { static: false }) map: MapsComponent
  constructor(private backend:BackendService,
    private dataService: DataBindingService,
    private router: Router,
    public dialog: MatDialog ,
    changeDetectorRef: ChangeDetectorRef, media: MediaMatcher) {
      this.mobileQuery = media.matchMedia('(max-width: 600px)');
      this._mobileQueryListener = () => changeDetectorRef.detectChanges();
      this.mobileQuery.addListener(this._mobileQueryListener);
      history.pushState(null, null, window.location.href);  
      history.pushState(null, null, window.location.href);
    }

  noOfMarker:number
  title = 'Jumbo GPS';
  date=[]
  types:Types[]=[
    {value: '1', viewValue: 'Salesperson'},
    {value: '2', viewValue: 'Truck'}
  ]
  dateChange(){    
    if (this.date[0] && this.date[1]){   
      this.backend.getAssetsBetweenTime(this.date[0],this.date[1]).subscribe( (assets)=>{ 
        this.dataService.changeData(assets)
      })    
    }
    else{
      this.backToNormal() 
    }
  }
  assets:AssetDetail[]
  ngOnInit(){
    if (!localStorage.getItem('token')) {
      this.router.navigate(['/login'])
    }
    this.backend.getAllAssets().subscribe( (assets)=>{ 
      this.assets=assets
    })
    this.noOfMarker=100
  }
  selectedType:string
  markers=[]
  typeChange(){
      this.backend.getAssetsByType(this.selectedType,this.noOfMarker).subscribe( (assets)=>{      
        this.dataService.changeData(assets)
      })
  }
  
  backToNormal(){
    this.backend.getAllAssets().subscribe( (assets)=>{ 
      this.dataService.changeData(assets)
    })   
    this.selectedType=""
    this.date=[]
    this.ID=''
  }
  
  ID:string=''
  showmarkerWithId(){
    if(this.ID==''){
      this.dataService.changeData(this.assets)
      return
    }
    this.dataService.changeData([])
    this.assets.forEach(asset => {
      if (asset.fkAssetId.pkAssetId.toString()==this.ID) {
        this.dataService.changeData([asset])
      }
    });
  } 
  firstName: string;
  secondName: string;
  password: string;
  email:string;
  username:string;
  role:string

  dialogService (){
    this.dialog.open(DialogboxComponent, {
      width: '40%',
      panelClass: 'dialog-container-custom' ,
      data: {firstName: this.firstName,secondName:this.secondName,password: this.password,email:this.email,username:this.username,role:this.role}
    }).afterClosed().subscribe(result => {
      this.backend.addNewUser(result).subscribe( (isSucess)=>{      
        if(isSucess == 'OK'){
          let payload = {};
          payload['title'] = 'New user';
          payload['body'] = 'User saved successfully';
          this.dialog.open(MessageDialogBoxComponent,{data:payload});
          this.currentMessage.next(payload);
        }
        else if(isSucess == 'CONFLICT')
        {
          let payload = {};
          payload['title'] = 'New user';
          payload['body'] = 'User already exists';
          console.log(payload);
          this.dialog.open(MessageDialogBoxComponent,{data:payload});
        }
        else
        {
          let payload = {};
          payload['title'] = 'New user';
          payload['body'] = 'User cannot be created';
          this.dialog.open(MessageDialogBoxComponent,{data:payload});
        }
      })
    });
  }
  public data: Array<any> = [{
    text:"Add User",
    icon:'user',
    click:()=> this.dialogService()
}, {
    text:"Deactivate User",
     icon:'user',
     click :()=>{
       this.backend.deactivateUser().subscribe((status)=>{
         if(status=='OK'){
           this.logout();
         }
       })
     }
}, {
  text:"Logout",
  icon:'logout',
  click:()=>{
    localStorage.removeItem('token');
    this.router.navigate(['/login'])
  }
},];
logout(){
  localStorage.removeItem('token');
  this.router.navigate(['/login'])
}
updateMarkerCount(a){
  this.noOfMarker=a
  this.backend.getAllAssetscount(a).subscribe((assets)=>{      
    this.dataService.changeData(assets)
  })
}
mobileQuery: MediaQueryList
private _mobileQueryListener: () => void;
ngOnDestroy(): void {
  this.mobileQuery.removeListener(this._mobileQueryListener);
}

}