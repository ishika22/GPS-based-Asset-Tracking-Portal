import { DatePipe } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { GoogleMap, MapInfoWindow, MapMarker } from '@angular/google-maps';
import * as data from "./data.json";
import TimeAgo from 'javascript-time-ago'
import en from 'javascript-time-ago/locale/en'

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
  vertices: google.maps.LatLngLiteral[] =[]
assetHistoryMarkerIcon={icon:'http://maps.google.com/mapfiles/kml/paddle/ylw-blank-lv.png'}
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

    
    
  }
  
  click(event: google.maps.MouseEvent) {
    console.log(event.latLng.toJSON() )
    this.vertices.push(event.latLng.toJSON())
    console.log(this.vertices)
  }
  openInfo(marker: MapMarker,data:any) {
    const type=data.fkAssetId.fkAssetType.assetType

    const name=data.fkAssetId.assetName
    const contactDetails = data.fkAssetId.assetContactDetail || 'NA'
    const date = this.pipe.transform(data.timeOfTracking, 'MMMM d, y, h:mm a');
    const timeAgo = new TimeAgo('en-US')
    const timeAgoString=timeAgo.format(Date.parse(data.timeOfTracking))
    
    const content = `
      <h2>${name}</h2><p><b>Last seen:</b>${timeAgoString}, ${date}</p>
      <p><b>Type:</b> ${type}<br/><b>Contact Details:</b> ${contactDetails}</p>
      <button>check history</button>
    `
    this.info.options={pixelOffset: new google.maps.Size(0, -30),content}

    
    this.info.open(marker)
    this.vertices=[{
      "lat": 19.151573603727645,
      "lng": 72.97737717732568
    },
      {
          "lat": 19.153273341047782,
          "lng": 72.96789450350683
      },
      {
          "lat": 19.15376281806577,
          "lng": 72.95660018920898
      },
      {
          "lat": 19.14082597939619,
          "lng": 72.94738096896093
      },
      {
          "lat": 19.126490591719953,
          "lng": 72.93850669270108
      },
      {
          "lat": 19.106459500309192,
          "lng": 72.93043860798429
      },
      {
          "lat": 19.092763133230946,
          "lng": 72.92606124287198
      },
      {
          "lat": 19.081164116712447,
          "lng": 72.9190231264169
      },
      {
          "lat": 19.0685375609046,
          "lng": 72.904867566344
      },
      {
          "lat": 19.059208380452834,
          "lng": 72.89122048687622
      },
      {
          "lat": 19.05036705841263,
          "lng": 72.87405816499117
      },
      {
          "lat": 19.039576428998497,
          "lng": 72.86092606965425
      },
      {
          "lat": 19.022861738957953,
          "lng": 72.85251466218355
      },
  ]
  }
  closeClick(){
    this.vertices=[]
  }

}
