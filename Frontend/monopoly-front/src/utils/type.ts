export interface Player {
    codeGame: number;
    nickName: string;
    dice1: number;
    dice2: number;
    position: number;
    cash: number;
    piece: Piece;
    turn: Turn;
}
export interface Piece {
    id: number;
    name: string;
}

export interface Turn {
    id: number;
    game: number;
    turn: number;
    active: boolean;
}