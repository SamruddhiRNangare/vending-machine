import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';

import { ItemsComponent } from './components/items/items.component';
import { MachineContextsComponent } from './components/machine-contexts/machine-contexts.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatButtonModule} from '@angular/material';
import {MatTableModule} from '@angular/material/table';
import {MatSortModule} from '@angular/material/sort';
import {MatMenuModule} from '@angular/material/menu';
import {MatIconModule} from '@angular/material/icon';
import {MatSelectModule} from '@angular/material/select';
import {MatToolbarModule} from '@angular/material/toolbar';


@NgModule({
  declarations: [
    AppComponent,
    ItemsComponent,
    MachineContextsComponent
  ],
  imports: [
    BrowserModule, HttpClientModule, BrowserAnimationsModule, MatButtonModule, MatTableModule, MatSortModule,
    MatMenuModule, MatIconModule, MatSelectModule, MatToolbarModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
