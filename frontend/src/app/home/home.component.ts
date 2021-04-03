import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AssetDetail } from '../AssetDetail';
import { BackendService } from '../backend.service';
import { DataBindingService } from '../data-binding.service';
import { MapsComponent } from '../maps/maps.component';

interface Types {
  value: string;
  viewValue: string;
}
interface DialogData {
  title:string
  username: string;
  password: string;
}
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  @ViewChild(MapsComponent, { static: false }) map: MapsComponent
  constructor(private backend:BackendService,
    private dataService: DataBindingService,
    private router: Router,
    public dialog: MatDialog ) {}

  
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
  }
  selectedType:string
  markers=[]
  typeChange(){
      this.backend.getAssetsByType(this.selectedType).subscribe( (assets)=>{      
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
  username: string;
  password: string;

  dialogService (){
    this.dialog.open(Dialog, {
      width: '250px',
      data: {username: this.username, password: this.password}
    }).afterClosed().subscribe(result => {
      console.log('The dialog was closed',result);
    });
  }
  public data: Array<any> = [{
    text:"Add User",
    icon:'user',
    click:()=> this.dialogService()
}, {
    text:"Deactivate User",
     icon:'user',
    // click :()=>{
    //   this.backend.deactivateUser().subscribe((status)=>{
    //     if(status=='OK'){
    //       this.logout();
    //     }
    //   })
    // }
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
}
@Component({
  selector: 'dialog-box',
  templateUrl: 'dialog.component.html',
})
export class Dialog {

  constructor(
    public dialogRef: MatDialogRef<Dialog>,
    @Inject(MAT_DIALOG_DATA) public data:DialogData) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

}