import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { Subscription } from 'rxjs/internal/Subscription';
import { MachineContext } from '../../models/machine-context';
import { MatTableDataSource } from '@angular/material/table';
import { MachineContextService } from '../../services/machine-context.service';

@Component({
  selector: 'app-machine-contexts',
  templateUrl: './machine-contexts.component.html',
  styleUrls: ['./machine-contexts.component.css']
})
export class MachineContextsComponent implements OnInit, OnDestroy {


  @ViewChild(MatSort) sort: MatSort;

  private contextsSubscription: Subscription;
  private deleteSubscription: Subscription;
  private deleteAllSubscription: Subscription;

  private contexts: Array<MachineContext>;
  public contextDataSource: MatTableDataSource<MachineContext>;
  public columnsToDisplay = ['menu', 'id', 'itemCount', 'balance', 'income', 'dateTime', 'state'];
  public height: number;

  constructor(private contextService: MachineContextService) {
    this.height = window.innerHeight - 210;
  }

  ngOnInit() {
    this.retrieveContexts();
  }

  ngOnDestroy(): void {
    if (this.contextsSubscription) {
      this.contextsSubscription.unsubscribe();
    }
    if (this.deleteSubscription) {
      this.deleteSubscription.unsubscribe();
    }
    if (this.deleteAllSubscription) {
      this.deleteAllSubscription.unsubscribe();
    }
  }

  private retrieveContexts(): void {
    this.contextsSubscription = this.contextService.retrieveContexts()
    .subscribe((response: Array<MachineContext>) => {
      this.contexts = response;
      this.contextDataSource = new MatTableDataSource<MachineContext>(this.contexts);
      this.contextDataSource.sort = this.sort;
    });
  }

  public deleteContext(context: MachineContext): void {
    this.deleteSubscription = this.contextService.deleteContext(context.id)
    .subscribe((response: boolean) => {
      if (response) {
        this.retrieveContexts();
      }
    });
  }

  // public deleteAllContexts(): void {
  //   this.deleteAllSubscription = this.contextService.deleteAll().subscribe();
  // }

}
