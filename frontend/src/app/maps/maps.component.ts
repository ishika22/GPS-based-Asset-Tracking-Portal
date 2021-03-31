import { DatePipe } from '@angular/common';
import { AfterViewInit, ChangeDetectorRef, Component, Input, OnInit, ViewChild } from '@angular/core';
import { GoogleMap, MapInfoWindow, MapMarker, MapPolygon } from '@angular/google-maps';
import TimeAgo from 'javascript-time-ago'
import en from 'javascript-time-ago/locale/en'
import { BackendService } from '../backend.service';
import { AssetDetail } from '../AssetDetail';
import { Subscription } from 'rxjs';
import { DataBindingService } from '../data-binding.service';

TimeAgo.addDefaultLocale(en)

@Component({
  selector: 'app-maps',
  templateUrl: './maps.component.html',
  styleUrls: ['./maps.component.css']
})
export class MapsComponent implements OnInit,AfterViewInit {
  @Input() markers=[]
  @ViewChild(GoogleMap, { static: false }) map: GoogleMap
  @ViewChild(MapInfoWindow, { static: false }) info: MapInfoWindow

  zoom=12
  center: google.maps.LatLngLiteral
  options: google.maps.MapOptions = {
    mapTypeId: 'roadmap',
    maxZoom: 18,
  }
  historySubscription: Subscription;
  
  constructor(private pipe : DatePipe,
              private backend:BackendService,
              private dataService: DataBindingService) {}
              
  icons: Record<string, { icon: string }> = {
    Salesperson: {
      icon: "http://maps.google.com/mapfiles/kml/shapes/man.png",
    },
    Truck: {
      icon: "http://maps.google.com/mapfiles/kml/shapes/truck.png",
    },
    History:{
      icon:'http://maps.google.com/mapfiles/kml/paddle/ylw-blank-lv.png'
    }
  } 
  vertices: google.maps.LatLngLiteral[] =[]
  selectedGeofence:MapPolygon
  drawingManger = new google.maps.drawing.DrawingManager({
    drawingControl: false,
    drawingControlOptions: {
      drawingModes:[ google.maps.drawing.OverlayType.POLYGON],
    },
    polygonOptions:{editable:true},
    drawingMode: google.maps.drawing.OverlayType.POLYGON,
  });
  
  ngOnInit(): void {
    //set center of map
    this.center = {
      lat: 19.119613,
      lng: 72.905306,      
    }
    //plot markers  on map
    this.backend.getAllAssets().subscribe((assets)=>this.convertAndPlotMarkers(assets))
    this.dataService.currentData().subscribe((data)=>this.convertAndPlotMarkers(data))   
  }
  
  ngAfterViewInit(){


  }
  
  convertAndPlotMarkers(assets:AssetDetail[]){
      this.markers=assets.map((i)=>{return{
        position: {
          lat:i.latitude,
          lng:i.longitude
        },
        options: {
          animation: google.maps.Animation.DROP,
          icon: this.icons[i.fkAssetId.fkAssetType.assetType].icon
        },
        data:i
      }})   
  }

  openInfo(marker: MapMarker,data:AssetDetail) {
    const type=data.fkAssetId.fkAssetType.assetType
    const name=data.fkAssetId.assetName
    const contactDetails = data.fkAssetId.assetContactDetail || 'NA'
    const date = this.pipe.transform(data.timeOfTracking, 'MMMM d, y, h:mm a');
    const timeAgo = new TimeAgo('en-US')
    const timeAgoString=timeAgo.format(Date.parse(data.timeOfTracking))
    const content =`
      <h2>${name}</h2><p><b>Last seen:</b>${timeAgoString}, ${date}</p>
      <p><b>Type:</b> ${type}<br/><b>Contact Details:</b> ${contactDetails}</p>
      <button id='history'">check history</button>
    `
    this.info.options={pixelOffset: new google.maps.Size(0, -30),content}  

    
    this.historySubscription=this.info.domready.subscribe(()=> 
      document.getElementById(`history`).addEventListener('click',()=>{
        this.loadHistory(data.fkAssetId.pkAssetId)
      })
    );
    this.info.open(marker)
  }

  loadHistory(id){
    this.backend.getAssetHistory(id).subscribe((
      points=>{this.vertices=points.map(i=>{return {'lat':i.latitude,'lng':i.longitude}})
      console.log(this.vertices);}
    ))
    this.historySubscription.unsubscribe()
    document.getElementById(`history`).remove()
  }
  closeClick(){
    this.vertices=[] 
    this.enableDrawing()   
  }
  closeDrawing(){
    this.drawingManger.setMap(null)
  }
  enableDrawing(){
    this.drawingManger.setMap(this.map.googleMap)
    google.maps.event.addListenerOnce(this.drawingManger, 'overlaycomplete', (polygon)=> {
      this.selectedGeofence = polygon;
      console.log(this.selectedGeofence);
      this.closeDrawing()
  });
  }

  
}
