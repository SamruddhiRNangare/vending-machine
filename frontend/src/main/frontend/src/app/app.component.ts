import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  public title = 'Vending';
  public itemView: boolean;

  constructor() {
    this.itemView = true;
  }

  public toggleView(): void {
    this.itemView = !this.itemView;
  }
}
