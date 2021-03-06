import { DatePipe } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { GoogleMap, MapInfoWindow, MapMarker } from '@angular/google-maps';
import * as data from "./data.json";

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
    minZoom: 12 ,
  }
  assetLogs:  any[]  = (data  as  any).default;
  constructor(private pipe : DatePipe) {}
  icons: Record<string, { icon: string }> = {
    Salesperson: {
      icon: "http://maps.google.com/mapfiles/kml/shapes/man.png",
    },
    Truck: {
      icon: "http://maps.google.com/mapfiles/kml/shapes/truck.png",
    }
  } 
  ngOnInit(): void {
    //set center of map
    this.center = {
      lat: 19.119613,
      lng: 72.905306,      
    }
    //make a list of markers to plot on map
    this.markers=this.assetLogs.map((i)=>{return{
      position: {
        lat:i.latitude,
        lng:i.longitude
      },
      options: {
        animation: google.maps.Animation.DROP,
        icon: this.icons[i.fkAssetId.fkAssetType.assetType].icon
      },
      name:i
    }})    

    console.log(this.markers)
    
  }
  
  click(event: google.maps.MouseEvent) {
    console.log(event)
  }
  openInfo(marker: MapMarker,data:any) {
    const type=data.fkAssetId.fkAssetType.assetType

    const name=data.fkAssetId.assetName
    const contactDetails = data.fkAssetId.assetContactDetail || 'NA'
    const date = this.pipe.transform(data.timeOfTracking, 'MMMM d, y, h:mm:ss a');
    const content = `
      <h2>${name}</h2><p>${date}</p>
      <p><b>Type:</b> ${type}<br/><b>Contact Details:</b> ${contactDetails}</p>
      <button>check history</button>
    `
    this.info.options={pixelOffset: new google.maps.Size(0, -30),content}
    this.info.open(marker)
    
  }

}
