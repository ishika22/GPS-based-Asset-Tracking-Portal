import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { AssetDetail, Coordinates } from './AssetDetail';
import { AssetDetails, history } from './maps/mock-data';
@Injectable({
  providedIn: 'root'
})
export class BackendService {

  constructor(private http:HttpClient) {
    
   }
  serverURL='localhost'

  getAllAssets():Observable<AssetDetail[]>{
    const url = `${this.serverURL}/location/list`;
    const Assets = of(AssetDetails);//this.http.get<AssetDetails[]>(url)

    return Assets     
  }

  getAssetHistory(id:number):Observable<Coordinates[]>{
    const url = `${this.serverURL}/location/list`;
    const AssetHistory = of(history);//this.http.get<AssetDetails[]>(url)
    return AssetHistory;
  }

}
