import { CategoriaModel } from "./categoria.model";

export interface ItemModel {
    id: number;
    descricao: string;
    categoria: CategoriaModel;
    valor: number;
}
