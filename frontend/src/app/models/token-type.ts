import { Injectable } from "@angular/core";

@Injectable()
export class TokenType {
    id: number = 0;
    sub: string = '';
    username: string = '';
    roles: string[] = [];
    permissions: string[] = [];
    iat: number = 123;
    exp: number = 456;
}
