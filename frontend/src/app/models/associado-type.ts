import { Token } from "@angular/compiler";
import { TokenType } from "./token-type";

export type AssociadoType = {
   id: number;
   email: string;
   nome: string;
   telefone: string;
   userData?: TokenType;
   accessToken?: string;
   stats: { title: string; value: number }[];
   logs: string[];
}
