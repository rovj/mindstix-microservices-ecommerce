import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Inventory} from "../../model/inventory";
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {

  constructor(private httpClient: HttpClient) {
  }

  createInventory(inventory: Inventory): Observable<Inventory> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    return this.httpClient.post<Inventory>('http://localhost:9000/api/inventory', inventory, httpOptions);
  }

  getStockCount(skuCode : String) : Observable<Number> {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    return this.httpClient.get<Number>(`http://localhost:9000/api/inventory/getStockCount?skuCode=${skuCode}`,httpOptions);
  }
}
