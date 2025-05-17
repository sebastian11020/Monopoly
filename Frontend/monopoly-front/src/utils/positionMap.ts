export interface BoardPosition {
    x: number;
    y: number;
}

export const boardPositions: BoardPosition[] = [

    { x: 92, y: 92 },  // 0


    { x: 82, y: 90 },  // 1
    { x: 74, y: 90 },  // 2
    { x: 66, y: 90 },  // 3
    { x: 57, y: 90 },  // 4
    { x: 49, y: 90 },  // 5
    { x: 41, y: 90 },  // 6
    { x: 32, y: 90 },  // 7
    { x: 24, y: 90 },  // 8
    { x: 16, y: 90 },  // 9

    // Esquina inferior izquierda
    { x: 5, y: 90 },   // 10

    // Línea izquierda (de abajo hacia arriba)
    { x: 5, y: 81 },   // 11
    { x: 5, y: 73 },   // 12
    { x: 5, y: 65 },   // 13
    { x: 5, y: 57 },   // 14
    { x: 5, y: 49 },   // 15
    { x: 5, y: 40 },   // 16
    { x: 5, y: 32 },   // 17
    { x: 5, y: 24 },    // 18
    { x: 5, y: 16 },   // 19
    // Esquina superior izquierda
    { x: 5, y: 5 },   // 20
    // Línea superior (de izquierda a derecha)
    { x: 16, y: 5 },   // 21
    { x: 24, y: 5 },   // 22
    { x: 33, y: 5 },   // 23
    { x: 41, y: 5 },   // 24
    { x: 49, y: 5 },   // 25
    { x: 57, y: 5 },   // 26
    { x: 65, y: 5 },   // 27
    { x: 73, y: 5 },  // 28
    { x: 81, y: 5 },  // 29

    // Esquina superior derecha
    { x: 92, y: 5 },  // 30

    // Línea derecha (de arriba hacia abajo)
    { x: 92, y: 15 },  // 31
    { x: 92, y: 23 },  // 32
    { x: 92, y: 31 },  // 33
    { x: 92, y: 40 },  // 34
    { x: 92, y: 48 },  // 35
    { x: 92, y: 56 },  // 36
    { x: 92, y: 65 },  // 37
    { x: 92, y: 73 },  // 38
    { x: 92, y: 81 },  // 39

];
