import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import {catchError} from 'rxjs/operators'; 
import { Observable, of } from 'rxjs';
import { Router } from '@angular/router';
@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  constructor(private router: Router) {}
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const idToken=localStorage.getItem('token')
    
    if (idToken) {
        request = request.clone({
            setHeaders: {
              Authorization: `Bearer ${idToken}`
            }
          });
        }
    return next.handle(request).pipe(
      catchError(
        (err, caught) => {
          if (err.status === 403){
            this.handleAuthError();
            return of(err);
          }
          throw err;
        }
      )
    );
  }
  private handleAuthError() {
    localStorage.removeItem('token');
    this.router.navigate(['/login']);
  };
}