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
import { HttpClientModule } from '@angular/common/http';
import {MatSelectModule} from '@angular/material/select';
import {MatInputModule} from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import { ErrorStateMatcher } from '@angular/material/core';
import { LoginComponent } from './login/login.component';
import { HomeComponent } from './home/home.component';
@NgModule({

  declarations: [
    AppComponent,
    MapsComponent,
    SearchBoxComponent,
    LoginComponent,
    HomeComponent
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
    MatButtonModule
    
  ],
  providers: [DatePipe,TimeAgoPipe,{provide: OWL_DATE_TIME_LOCALE, useValue: 'en-IN'},ErrorStateMatcher],
  bootstrap: [AppComponent]
})
export class AppModule { }
