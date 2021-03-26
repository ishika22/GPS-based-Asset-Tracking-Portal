import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataBindingService {

  constructor() { }
  private data = new BehaviorSubject<any[]>([]);

  //this is what your components subsribes to
  currentData() : Observable<any[]> {
     return this.data.asObservable();
  }

  //this function allows you to change the value to be accessed by other components
  changeData(data: any[]) {
    this.data.next(data);    
  }
}
