import { Injectable } from '@angular/core';
import { ItemModel } from '../models/item.model';

@Injectable({ providedIn: 'root' })
export class ItemService {
    private itens: ItemModel[] = [
        {
            id: 1,
            descricao: 'Água Mineral 350ml',
            categoria: { id: 1, categoria: 'Bebida' },
            valor: 3.00
        },
        {
            id: 2,
            descricao: 'Refrigerante Lata',
            categoria: { id: 1, categoria: 'Bebida' },
            valor: 5.50
        },
        {
            id: 3,
            descricao: 'Chocolate',
            categoria: { id: 2, categoria: 'Doce' },
            valor: 4.00
        }
    ];

    listarPorCategoria(categoria: string): ItemModel[] {
        return this.itens.filter(item => item.categoria.categoria === categoria);
    }
}
