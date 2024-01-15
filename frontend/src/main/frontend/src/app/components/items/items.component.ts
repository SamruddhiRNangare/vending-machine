import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Subscription } from 'rxjs/internal/Subscription';
import { ItemService } from '../../services/item.service';
import { Item } from 'src/app/models/item';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MachineContext } from '../../models/machine-context';
import { MachineContextService } from '../../services/machine-context.service';

@Component({
  selector: 'app-items',
  templateUrl: './items.component.html',
  styleUrls: ['./items.component.css']
})
export class ItemsComponent implements OnInit, OnDestroy {

  @ViewChild(MatSort) sort: MatSort;

  private itemsSubscription: Subscription;
  private insertSubscription: Subscription;
  private contextSubscription: Subscription;
  private purchaseSubscription: Subscription;
  private deleteSubscription: Subscription;
  private coinSubscription: Subscription;
  private refundSubscription: Subscription;

  private items: Array<Item>;
  public context: MachineContext;

  public itemsDataSource: MatTableDataSource<Item>;
  public columnsToDisplay = ['menu', 'id', 'type', 'price', 'insertDateTime', 'purchaseDateTime'];

  public nbrOfItems: string;
  public height: number;

  constructor(private itemService: ItemService,
              private contextService: MachineContextService) {
    this.nbrOfItems = '0';
    this.height = window.innerHeight - 260;
   }

  ngOnInit() {
    this.retrieveItemList();
    this.retrieveLatestContext();
  }

  ngOnDestroy(): void {
    if (this.itemsSubscription) {
      this.itemsSubscription.unsubscribe();
    }
    if (this.insertSubscription) {
      this.insertSubscription.unsubscribe();
    }
    if (this.contextSubscription) {
      this.contextSubscription.unsubscribe();
    }
    if (this.purchaseSubscription) {
      this.purchaseSubscription.unsubscribe();
    }
    if (this.deleteSubscription) {
      this.deleteSubscription.unsubscribe();
    }
    if (this.coinSubscription) {
      this.coinSubscription.unsubscribe();
    }
    if (this.refundSubscription) {
      this.refundSubscription.unsubscribe();
    }
  }

  private retrieveItemList(): void {
    this.itemsSubscription = this.itemService.retrieveItems()
    .subscribe((response: Array<Item>) => {
      // if (response) {
        this.items = response;
        this.itemsDataSource = new MatTableDataSource<Item>(this.items);
        this.itemsDataSource.sort = this.sort;
      // }
    });
  }

  private retrieveLatestContext(): void {
    this.contextSubscription = this.contextService.retrieveContexts()
    .subscribe((response: Array<MachineContext>) => {
      if (response) {
        this.context = response[response.length - 1];
      }
    });
  }

  public purchase(item: Item): void {
    this.purchaseSubscription = this.itemService.purchaseItem(item.id)
    .subscribe((response: MachineContext) => {
      if (response) {
        console.log('purchase => ' + JSON.stringify(response));
        this.context = response;
        this.retrieveItemList();
      }
    });
  }

  public delete(item: Item) {
    this.deleteSubscription = this.itemService.deleteItem(item.id)
    .subscribe((response: MachineContext) => {
      if (response) {
        this.context = response;
        this.retrieveItemList();
      }
    });
  }

  public insertItems(): void {
    if (this.nbrOfItems) {
      const newItems = new Array<Item>();
      for (let i = 0; i < Number(this.nbrOfItems); i++) {
        const item = new Item();
        item.price = 1.25;
        item.type = 'Soda';
        newItems.push(item);
      }
      this.insertSubscription = this.itemService.insertItems(newItems)
      .subscribe((response: MachineContext) => {
        if (response) {
          this.context = response;
          this.retrieveItemList();
        }
      });
    }
  }

  public insertCoin(): void {
    this.coinSubscription = this.contextService.insertQuarter()
    .subscribe((response: MachineContext) => {
      if (response) {
        this.context = response;
      }
    });
  }

  public refund(): void {
    this.refundSubscription = this.contextService.requestRefund(this.context.balance)
    .subscribe((response: MachineContext) => {
      if (response) {
        this.context = response;
      }
    });
  }
}
