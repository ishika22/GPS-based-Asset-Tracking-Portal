import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { GoogleMapsModule } from '@angular/google-maps';
import { MapsComponent } from './maps/maps.component';
import { DatePipe } from '@angular/common';
import { TimeAgoPipe } from 'time-ago-pipe';
import { SearchBoxComponent } from './search-box/search-box.component';
import { OwlDateTimeModule, OwlNativeDateTimeModule, OWL_DATE_TIME_LOCALE } from '@danielmoncada/angular-datetime-picker';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import {MatSelectModule} from '@angular/material/select';
import {MatInputModule} from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatSidenavModule} from '@angular/material/sidenav';
import { ErrorStateMatcher } from '@angular/material/core';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
import { TokenInterceptor } from './token.interceptor';
import { ButtonsModule } from '@progress/kendo-angular-buttons';
import { AngularFireMessagingModule } from '@angular/fire/messaging';
import { AngularFireDatabaseModule } from '@angular/fire/database';
import { AngularFireAuthModule } from '@angular/fire/auth';
import { AngularFireModule } from '@angular/fire';
import { DialogNotification, MessagingService } from './service/messaging.service';
import { environment } from '../environments/environment';
import { AsyncPipe } from '../../node_modules/@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { IncrementInputComponent } from './increment-input/increment-input.component';
import { ServiceWorkerModule } from '@angular/service-worker';


@NgModule({

  declarations: [
    AppComponent,
    MapsComponent,
    SearchBoxComponent,
    LoginComponent,
    HomeComponent,
    DialogNotification,
    IncrementInputComponent,
    DialogboxComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    GoogleMapsModule,
  FormsModule,
  ReactiveFormsModule,
    OwlNativeDateTimeModule,
    OwlDateTimeModule,
    HttpClientModule,
    MatSelectModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatButtonModule,
    ButtonsModule,
    AngularFireAuthModule,
      AngularFireMessagingModule,
      AngularFireModule.initializeApp(environment.firebase)   ,
      MatDialogModule ,
      MatFormFieldModule,
      MatSidenavModule,
      ServiceWorkerModule.register('ngsw-worker.js', {
        enabled: environment.production,
        registrationStrategy: 'registerWhenStable:30000'
      }),
  ],
  
  providers: [DatePipe,TimeAgoPipe,{provide: OWL_DATE_TIME_LOCALE, useValue: 'en-IN'},ErrorStateMatcher,{
    provide: HTTP_INTERCEPTORS,
    useClass: TokenInterceptor,
    multi: true
  },MessagingService,AsyncPipe],

  bootstrap: [AppComponent],
  entryComponents:[DialogNotification]
})
export class AppModule { }
