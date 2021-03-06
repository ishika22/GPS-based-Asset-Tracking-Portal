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
  constructor(private pipe : DatePipe) { }
  
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
        icon: "http://maps.google.com/mapfiles/kml/shapes/truck.png"
      },
      name:i.timeOfTracking
    }})    

    console.log(this.markers)
    
  }
  
  click(event: google.maps.MouseEvent) {
    console.log(event)
  }
  openInfo(marker: MapMarker,name:string) {
    const description='loagnaogaognagn aajfofajoa'
    const hours='car'
    const phone='3'
    name = this.pipe.transform(name, 'MMMM d, y, h:mm:ss a');
    const content = `
      <h2>${name}</h2><p>${description}</p>
      <p><b>Type:</b> ${hours}<br/><b>Phone:</b> ${phone}</p>
      <button>check history</button>
    `
    console.log(marker);
    this.info.options={pixelOffset: new google.maps.Size(0, -30),content}
    this.info.open(marker)
    
  }

}
