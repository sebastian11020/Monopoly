export interface Player {
    nickName: string;
    dice1: number;
    dice2: number;
    position: number;
    cash: number;
    piece: {
        id: number;
        name: string;
    };
    turn: {
        id: number;
        game: number;
        turn: number;
        active: boolean;
    };
}
