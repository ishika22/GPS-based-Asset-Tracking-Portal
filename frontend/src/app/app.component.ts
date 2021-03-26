import { Component, Input, Output, SimpleChanges, ViewChild } from '@angular/core';
import { BackendService } from './backend.service';
import { DataBindingService } from './data-binding.service';
import { MapsComponent } from './maps/maps.component';

interface Types {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  @ViewChild(MapsComponent, { static: false }) map: MapsComponent
  constructor(private backend:BackendService,
    private dataService: DataBindingService) {}

  title = 'Jumbo GPS';
  date=[]
  types:Types[]=[
    {value: '1', viewValue: 'Salesperson'},
    {value: '2', viewValue: 'Truck'}
  ]
  dateChange(){
    console.log(this.date);
    
    if (this.date[0] && this.date[1]){   
      this.backend.getAssetsBetweenTime(this.date[0],this.date[1]).subscribe( (assets)=>{ 
        this.dataService.changeData(assets)
      })    
    }
    else{
      this.backend.getAllAssets().subscribe( (assets)=>{ 
        this.dataService.changeData(assets)
      })    
    }
  }

  selectedType:string
  markers=[]
  typeChange(){
      this.backend.getAssetsByType(this.selectedType).subscribe( (assets)=>{      
        this.dataService.changeData(assets)
      })
  }
  ID:string=''
}
