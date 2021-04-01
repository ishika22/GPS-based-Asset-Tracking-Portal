import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { AssetDetail } from '../AssetDetail';
import { BackendService } from '../backend.service';
import { DataBindingService } from '../data-binding.service';
import { MapsComponent } from '../maps/maps.component';

interface Types {
  value: string;
  viewValue: string;
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
    private router: Router) {}

  
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
    this.dataService.changeData([])
    this.assets.forEach(asset => {
      if (asset.fkAssetId.pkAssetId.toString()==this.ID) {
        this.dataService.changeData([asset])
      }
    });
  }

  public data: Array<any> = [{
    text:"Add User",
  
}, {
    text:"Deactivate User",
}, {
  text:"Logout",
  click:()=>{
    localStorage.removeItem('token');
    this.router.navigate(['/login'])
  }
},];
}
