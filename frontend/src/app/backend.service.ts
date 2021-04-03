import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { AssetDetail, AssetHistory } from './AssetDetail';
import { AssetDetails, history } from './maps/mock-data';
@Injectable({
  providedIn: 'root'
})
export class BackendService {

  constructor(private http:HttpClient) {
    
   }
  serverURL='http://localhost:8087';
  // httpOptions = {
  //   headers: new HttpHeaders({
  //     'Content-Type':  'application/json',
  //     Authorization: "Bearer " + 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpc2hpa2EuYWdhcndhbCIsImV4cCI6MTYxNzA1Mjg5NiwiaWF0IjoxNjE3MDQ5Mjk2fQ.e7PNPX9YvgGbBJTrem8v87TtgbfBioaOTNhpwMHxPR8'
  //   })
  // };

  getAllAssets():Observable<AssetDetail[]>{
    const url = `${this.serverURL}/location/list`;
    const Assets = this.http.get<AssetDetail[]>(url)  //of(AssetDetails);
    return Assets     
  }

  getAssetHistory(id:number):Observable<AssetHistory[]>{
    const url = `${this.serverURL}/location/id?id=${id}`;
    const AssetHistory = this.http.get<AssetHistory[]>(url)
    return AssetHistory;
  }
  getAssetsByType(typeId:string):Observable<AssetDetail[]>{
    const url = `${this.serverURL}/location/type?type=${typeId}`;
    const Assets = this.http.get<AssetDetail[]>(url)
    return Assets;
  }
  getAssetsBetweenTime(start:Date,end:Date):Observable<AssetDetail[]>{
    const url = `${this.serverURL}/location/time?startTime=${start.toISOString()}&endTime=${end.toISOString()}`;
    const Assets = this.http.get<AssetDetail[]>(url)
    return Assets;
  }
  autheticateUser(username:string,password:string):Observable<any>{
    const url = `${this.serverURL}/user/loginUser`;
    let data = {  username,password };
    const isValid = this.http.post<any>(url,data)
    return isValid;
  }

  pushAnomly(pkAssetId:string,anomalyDetectionCoordinates:string):Observable<any>{
    const url = `${this.serverURL}/anomaly/coordinates`;
    let data = {  pkAssetId,anomalyDetectionCoordinates };
    const isValid = this.http.post<any>(url,data)
    return isValid;
  }
    pushGeofence(pkAssetId:string,geoFencingCoordinates:string):Observable<any>{
    const url = `${this.serverURL}/geofencing/coordinates`;
    let data = { pkAssetId,geoFencingCoordinates };
    const isValid = this.http.post<any>(url,data)
    return isValid;
    }
  deactivateUser():Observable<any>{
    const url = `${this.serverURL}/user/deactiveUser`;
    const status = this.http.get<any>(url)
    return status;
  }
}
