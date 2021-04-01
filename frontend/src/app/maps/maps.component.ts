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

  center: google.maps.LatLngLiteral
  options: google.maps.MapOptions = {
    mapTypeId: 'roadmap',
    maxZoom: 18,
  }

  historySubscription: Subscription;
  geofenceButtonListner: Subscription;
  anomalyButton:Subscription

  originInput:HTMLInputElement
  destinationInput:HTMLInputElement
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
  
  
  selectedGeofence:google.maps.Polygon
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

    
    this.originInput = document.getElementById("origin-input") as HTMLInputElement;
    this.destinationInput = document.getElementById("destination-input") as HTMLInputElement;
    // const submitRoute = document.getElementById("submitRoute") as HTMLInputElement;
    const originAutocomplete = new google.maps.places.Autocomplete(this.originInput,{fields:["place_id"]});
    const destinationAutocomplete = new google.maps.places.Autocomplete(this.destinationInput,{fields:["place_id"]});
    // submitRoute.hidden=true
    
    console.log(this.map.controls);
    
    // originInput.hidden=true
    // destinationInput.hidden=true
      this.setupPlaceChangedListener(originAutocomplete, "ORIG");
    this.setupPlaceChangedListener(destinationAutocomplete, "DEST");
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
  
  vertices: google.maps.LatLngLiteral[] =[]
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
      <button id='geofence'">plot geofence</button>
      <button id='anomaly'">mark route</button>
    `
    this.info.options={pixelOffset: new google.maps.Size(0, -30),content}  

    
    this.historySubscription=this.info.domready.subscribe(()=> 
      document.getElementById(`history`).addEventListener('click',()=>{
        this.loadHistory(data.fkAssetId.pkAssetId)
      },{ once: true })
    );
    this.geofenceButtonListner=this.info.domready.subscribe(()=> 
    document.getElementById(`geofence`).addEventListener('click',()=>{
      this.enableDrawing()
    },{ once: true })
  );
  this.anomalyButton=this.info.domready.subscribe(()=> 
    document.getElementById(`anomaly`).addEventListener('click',()=>{
      this.enableRouteInput()
    },{ once: true })
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
    this.selectedGeofence?.setMap(null)
    this.closeDrawing()
    this.disableRouteInput()
  }
  closeDrawing(){
    this.drawingManger.setMap(null)
  }
  enableDrawing(){
    this.drawingManger.setMap(this.map.googleMap)

    var element = <HTMLInputElement> document.getElementById(`geofence`);
    element.disabled = true;
    element.innerText='submit'
    element.addEventListener('click',()=>{
     console.log('submit',google.maps.geometry.encoding.encodePath(this.selectedGeofence.getPath()));
     element.hidden=true;
     this.selectedGeofence.setEditable(false)
    },{ once: true })

    google.maps.event.addListenerOnce(this.drawingManger, 'overlaycomplete', (polygon)=> {
      this.selectedGeofence = polygon.overlay;
      console.log(this.selectedGeofence);
      this.closeDrawing()
      element.disabled = false;
  });
  }

  //Route plotting
  originPlaceId: string="";
  destinationPlaceId: string="";
  directionsService: google.maps.DirectionsService= new google.maps.DirectionsService();
  directionsRenderer: google.maps.DirectionsRenderer= new google.maps.DirectionsRenderer();
  setupPlaceChangedListener(
    autocomplete: google.maps.places.Autocomplete,
    mode: string
  ) {
    autocomplete.bindTo("bounds", this.map.googleMap);

    autocomplete.addListener("place_changed", () => {
      const place = autocomplete.getPlace();

      if (!place.place_id) {
        window.alert("Please select an option from the dropdown list.");
        return;
      }

      if (mode === "ORIG") {
        this.originPlaceId = place.place_id;
      } else {
        this.destinationPlaceId = place.place_id;
      }
      this.route();
    });
  }

  route() {
    if (!this.originPlaceId || !this.destinationPlaceId) {
      return;
    }
    const me = this;

    this.directionsService.route(
      {
        origin: { placeId: this.originPlaceId },
        destination: { placeId: this.destinationPlaceId },
        travelMode: google.maps.TravelMode.DRIVING,
        provideRouteAlternatives:true
      },
      (response, status) => {
        if (status === "OK") {
          me.directionsRenderer.setDirections(response);
          this.directionsRenderer.setMap(this.map.googleMap);
          console.log(response);
          const anomalyButton=document.getElementById(`anomaly`) as HTMLInputElement
          anomalyButton.disabled=false
        } else {
          window.alert("Directions request failed due to " + status);
        }
      }
    );
  }

  enableRouteInput(){
    if(this.map.controls[google.maps.ControlPosition.TOP_LEFT].getLength()==0){
      this.map.controls[google.maps.ControlPosition.TOP_LEFT].push(this.originInput)
      this.map.controls[google.maps.ControlPosition.TOP_LEFT].push(this.destinationInput)
  }
    else{
      this.originInput.hidden=false
      this.destinationInput.hidden=false
    }
    // const submitRoute = document.getElementById("submitRoute") as HTMLInputElement;
    this.originInput.value=""
    this.originInput.hidden=false
    this.destinationInput.value=""
    this.destinationInput.hidden=false
    // this.map.controls[google.maps.ControlPosition.TOP_LEFT].push(submitRoute)

    const anomalyButton=document.getElementById(`anomaly`) as HTMLInputElement
    anomalyButton.disabled = true
    anomalyButton.innerText='submit route'
    anomalyButton.addEventListener('click',()=>{
      console.log('submit',this.directionsRenderer.getDirections().routes[this.directionsRenderer.getRouteIndex()].overview_polyline);
      anomalyButton.hidden=true
      console.log(this.directionsRenderer);
      
      this.disableRouteInput()
     },{ once: true })
     
  }
  disableRouteInput(){
    this.originInput.hidden=true
    this.destinationInput.hidden=true
    this.directionsRenderer.setMap(null)

  }
  
  
  
}
