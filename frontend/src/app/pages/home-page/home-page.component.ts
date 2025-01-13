import {Component, inject, OnInit} from '@angular/core';
import {OidcSecurityService} from "angular-auth-oidc-client";
import {Product} from "../../model/product";
import {ProductService} from "../../services/product/product.service";
import {AsyncPipe, JsonPipe} from "@angular/common";
import {Router} from "@angular/router";
import {Order} from "../../model/order";
import {FormsModule} from "@angular/forms";
import {OrderService} from "../../services/order/order.service";
import { InventoryService } from '../../services/inventory/inventory.service';
import { Inventory } from '../../model/inventory';

@Component({
  selector: 'app-homepage',
  templateUrl: './home-page.component.html',
  standalone: true,
  imports: [
    AsyncPipe,
    JsonPipe,
    FormsModule
  ],
  styleUrl: './home-page.component.css'
})
export class HomePageComponent implements OnInit {
  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly productService = inject(ProductService);
  private readonly orderService = inject(OrderService);
  private readonly inentoryService = inject(InventoryService);
  private readonly router = inject(Router);
  isAuthenticated = false;
  products: Array<Product> = [];
  quantityIsNull = false;
  orderSuccess = false;
  orderFailed = false;

  ngOnInit(): void {
    this.oidcSecurityService.isAuthenticated$.subscribe(
      ({isAuthenticated}) => {
        this.isAuthenticated = isAuthenticated;
        this.productService.getProducts()
          .pipe()
          .subscribe(product => {
            this.products = product;
            console.log(this.products,product);
          })
      }
    )
  }

  goToCreateProductPage() {
    this.router.navigateByUrl('/add-product');
  }

  orderProduct(product: Product, quantity: string) {

    this.oidcSecurityService.userData$.subscribe(result => {
      const userDetails = {
        email: result.userData.email,
        firstName: result.userData.given_name ? result.userData.given_name : 'Varun',
        lastName: result.userData.family_name ? result.userData.family_name : 'Jaiswal'
      };

      console.log(userDetails,result);

      if(!quantity) {
        this.orderFailed = true;
        this.orderSuccess = false;
        this.quantityIsNull = true;
      } else {
        const order: Order = {
          skuCode: product.skuCode,
          price: product.price,
          quantity: Number(quantity),
          userDetails: userDetails
        }

        console.log(order);

        this.orderService.orderProduct(order).subscribe(() => {
          this.inentoryService.getStockCount(product.skuCode).subscribe(stockCount => {
            const inventory: Inventory = {
              skuCode: product.skuCode,
              quantity: (Number(stockCount) - Number(quantity))
            }
            this.inentoryService.createInventory(inventory).subscribe(inventory => {
              this.orderSuccess = true;
              this.orderFailed = false;
              console.log("Order placed successfully");
            },error => {
              this.orderFailed = true;
              this.orderSuccess = false;
              console.log("Order failed");
            });
          },err => {
              this.orderFailed = true;
              this.orderSuccess = false;
              console.log("Order failed");
          });
        }, error => {
          this.orderFailed = true;
          this.orderSuccess = false;
          console.log("Order failed");
        })
      }
    })
  }
}
