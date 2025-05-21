export interface Player {
    codeGame: number;
    nickName: string;
    dice1: number;
    dice2: number;
    position: number;
    cash: number;
    piece: Piece;
    turn: Turn;
    gameState: GameState;
    namesCards?: string[];
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

export interface GameState {
    nickName: string;
    type?: 'PROPERTY' | 'TRANSPORT' | 'SERVICE' | 'Card' | 'TAXES' | string;
    statePosition?: 'DISPONIBLE' | 'COMPRADA' | 'HIPOTECADA' | 'ESPECIAL'|string;
    message?: string;
    gamePlayers: any[];
    [key: string]: any;
}

export interface Buy {
    nickName: string;
    sucess: boolean;
    message: string;
}