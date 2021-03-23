import { HttpClient } from '@angular/common/http';
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
  serverURL='http://localhost:8087'

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

}
