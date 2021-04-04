import { Component, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { AssetDetail } from './AssetDetail';
import { BackendService } from './backend.service';
import { DataBindingService } from './data-binding.service';
import { MapsComponent } from './maps/maps.component';
import { AssetDetails } from './maps/mock-data';
import { MessagingService } from './service/messaging.service';

interface Types {
  value: string;
  viewValue: string;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  @ViewChild(MapsComponent, { static: false }) map: MapsComponent
  constructor(private backend:BackendService,
    private dataService: DataBindingService,
    private messagingService: MessagingService) {}

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
  message
  ngOnInit(){
    this.backend.getAllAssets().subscribe( (assets)=>{ 
      this.assets=assets
    })
    this.messagingService.requestPermission()
  this.messagingService.receiveMessage()
  this.message = this.messagingService.currentMessage
  }
  selectedType:string
  markers=[]

  
  backToNormal(){
    this.backend.getAllAssets().subscribe( (assets)=>{ 
      this.dataService.changeData(assets)
    })   
    this.selectedType=""
    this.date=[]
    this.ID=''
  }
  filteredData:AssetDetail[]=AssetDetails
  ID:string=''
  showmarkerWithId(){
    this.dataService.changeData([])
    this.assets.forEach(asset => {
      if (asset.fkAssetId.pkAssetId.toString()==this.ID) {
        this.dataService.changeData([asset])
      }
    });
  }

  
}
