import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Item } from '../models/item';
import { Observable } from 'rxjs/internal/Observable';
import { empty } from 'rxjs/internal/observable/empty';
import { MachineContext } from '../models/machine-context';

@Injectable({
  providedIn: 'root'
})
export class ItemService {

  private url = '/api/machine/items';
  private headers = {headers: new HttpHeaders({ 'Content-Type': 'application/json' })};

  constructor(private http: HttpClient) { }

  public retrieveItems(): Observable<Array<Item>> {
    return this.http.get<Array<Item>>(this.url);
  }


  public retrieveItem(itemId: number): Observable<Item> {
    if (!itemId) {
      return empty();
    }
    return this.http.get<Item>(this.url + '/' + itemId);
  }

  public insertItems(items: Array<Item>): Observable<MachineContext> {
    if (!items) {
      return empty();
    }
    const itemsStr = JSON.stringify(items);
    return this.http.post<MachineContext>(this.url, itemsStr, this.headers);
  }

  public purchaseItem(itemId: number): Observable<MachineContext> {
    if (!itemId) {
      return empty();
    }

    return this.http.put<MachineContext>(this.url + '/' + itemId, null, this.headers);
  }

  public deleteItem(itemId: number): Observable<MachineContext> {
    if (!itemId) {
      return empty();
    }
    return this.http.delete<MachineContext>(this.url + '/' + itemId);
  }
}
