import {Component, inject} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {Product} from "../../model/product";
import {ProductService} from "../../services/product/product.service";
import { InventoryService } from '../../services/inventory/inventory.service';
import {NgIf} from "@angular/common";
import { Inventory } from '../../model/inventory';

@Component({
  selector: 'app-add-product',
  standalone: true,
  imports: [ReactiveFormsModule, NgIf],
  templateUrl: './add-product.component.html',
  styleUrl: './add-product.component.css'
})
export class AddProductComponent {
  addProductForm: FormGroup;
  private readonly productService = inject(ProductService);
  private readonly inventoryService = inject(InventoryService);
  productCreated = false;

  constructor(private fb: FormBuilder) {
    this.addProductForm = this.fb.group({
      skuCode: ['', [Validators.required]],
      name: ['', [Validators.required]],
      description: ['', [Validators.required]],
      price: [0, [Validators.required]],
      quantity: [0, [Validators.required]]
    })
  }

  onSubmit(): void {
    if (this.addProductForm.valid) {
      const product: Product = {
        skuCode: this.addProductForm.get('skuCode')?.value,
        name: this.addProductForm.get('name')?.value,
        description: this.addProductForm.get('description')?.value,
        price: this.addProductForm.get('price')?.value
      }
      const inventory: Inventory = {
        skuCode: this.addProductForm.get('skuCode')?.value,
        quantity: this.addProductForm.get('quantity')?.value
      }
      this.inventoryService.createInventory(inventory).subscribe(inventory => {
        console.log("Inventory created");
        this.productService.createProduct(product).subscribe(product => {
          this.productCreated = true;
          this.addProductForm.reset();
        },err => {
          this.productCreated = false;
        })
      },err => {
        this.productCreated = false;
      })
    } else {
      console.log('Form is not valid');
    }
  }

  get skuCode() {
    return this.addProductForm.get('skuCode');
  }

  get name() {
    return this.addProductForm.get('name');
  }

  get description() {
    return this.addProductForm.get('description');
  }

  get price() {
    return this.addProductForm.get('price');
  }

  get quantity() {
    return this.addProductForm.get('quantity')
  }
}
