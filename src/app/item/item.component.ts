import { Component } from '@angular/core';
import { ItemService } from './item.service';
import { ItemModel } from '../models/item.model';

@Component({
    selector: 'app-item',
    templateUrl: './item.component.html',
})
export class ItemComponent {
    categoriaSelecionada = 'Bebida';
    itens: ItemModel[];

    constructor(private itemService: ItemService) {
        this.itens = this.itemService.listarPorCategoria(this.categoriaSelecionada);
    }
}
