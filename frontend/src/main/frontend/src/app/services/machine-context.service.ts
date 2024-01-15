import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MachineContext } from '../models/machine-context';
import { Observable } from 'rxjs/internal/Observable';
import { empty } from 'rxjs/internal/observable/empty';
import { tap } from 'rxjs/operators';


@Injectable({
  providedIn: 'root'
})
export class MachineContextService {

  private url = '/api/machine/contexts';
  private headers = {headers: new HttpHeaders({ 'Content-Type': 'application/json' })};

  constructor(private http: HttpClient) { }

  public retrieveContexts(): Observable<Array<MachineContext>> {
    return this.http.get<Array<MachineContext>>(this.url);
  }

  public retrieveContext(contextId: number): Observable<MachineContext> {
    if (!contextId) {
      return empty();
    }
    return this.http.get<MachineContext>(this.url + '/' + contextId);
  }

  public insertQuarter(): Observable<MachineContext> {
    const value = .25;
    return this.http.put<MachineContext>(this.url + '/' + value, null, this.headers);
  }

  public requestRefund(balance: number): Observable<MachineContext> {
    const value = -balance;
    return this.http.put<MachineContext>(this.url + '/' + value, null, this.headers);
  }

  public deleteContext(contextId: number): Observable<boolean> {
    if (!contextId) {
      return empty();
    }
    return this.http.delete<boolean>(this.url + '/' + contextId);
  }
}
