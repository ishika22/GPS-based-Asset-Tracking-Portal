import { DatePipe } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { GoogleMap, MapInfoWindow, MapMarker } from '@angular/google-maps';
import TimeAgo from 'javascript-time-ago'
import en from 'javascript-time-ago/locale/en'
import { BackendService } from '../backend.service';
import { AssetDetail } from '../AssetDetail';

TimeAgo.addDefaultLocale(en)

@Component({
  selector: 'app-maps',
  templateUrl: './maps.component.html',
  styleUrls: ['./maps.component.css']
})
export class MapsComponent implements OnInit {
  @ViewChild(GoogleMap, { static: false }) map: GoogleMap
  @ViewChild(MapInfoWindow, { static: false }) info: MapInfoWindow
  
  
  markers = []
  zoom=12
  center: google.maps.LatLngLiteral
  options: google.maps.MapOptions = {
    mapTypeId: 'roadmap',
    maxZoom: 18,
//     minZoom: 12 , // max zoom-out limit
  }

  assetLogs:  AssetDetail[];
  constructor(private pipe : DatePipe,
              private backend:BackendService) {}
  icons: Record<string, { icon: string }> = {
    Salesperson: {
      icon: "http://maps.google.com/mapfiles/kml/shapes/man.png",
    },
    Truck: {
      icon: "http://maps.google.com/mapfiles/kml/shapes/truck.png",
    }
  } 
  vertices: google.maps.LatLngLiteral[] =[]
assetHistoryMarkerIcon={icon:'http://maps.google.com/mapfiles/kml/paddle/ylw-blank-lv.png'}
  ngOnInit(): void {
    //set center of map
    this.center = {
      lat: 19.119613,
      lng: 72.905306,      
    }
    //make a list of markers to plot on map
    this.backend.getAllAssets().subscribe(
      (assets)=>{
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
    )   
    
    
  }
  
  click(event: google.maps.MouseEvent) {
    console.log(event.latLng.toJSON() )
    this.vertices.push(event.latLng.toJSON())
    console.log(this.vertices)
  }
  openInfo(marker: MapMarker,data:AssetDetail) {
    const type=data.fkAssetId.fkAssetType.assetType

    const name=data.fkAssetId.assetName
    const contactDetails = data.fkAssetId.assetContactDetail || 'NA'
    const date = this.pipe.transform(data.timeOfTracking, 'MMMM d, y, h:mm a');
    const timeAgo = new TimeAgo('en-US')
    const timeAgoString=timeAgo.format(Date.parse(data.timeOfTracking))
    
    const content = `
      <h2>${name}</h2><p><b>Last seen:</b>${timeAgoString}, ${date}</p>
      <p><b>Type:</b> ${type}<br/><b>Contact Details:</b> ${contactDetails}</p>
      <button (click)="loadHistory(${data.fkAssetId.pkAssetId})">check history</button>
    `
    this.info.options={pixelOffset: new google.maps.Size(0, -30),content}

    
    this.info.open(marker)

  }

  loadHistory(id){
    this.backend.getAssetHistory(id).subscribe((
      points=>this.vertices=points
    ))
  }
  closeClick(){
    this.vertices=[]
  }

}
