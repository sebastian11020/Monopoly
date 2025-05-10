import { useEffect, useState } from 'react';
import Dice from './dice';
import Token from './token';
import { boardPositions } from '../utils/positionMap';
import * as React from "react";

interface Player {
    namePiece: string;
    position: number;
}

const Board = ({ players }: { players: Player[] }) => {
    const boardImage = '/assets/Tablero.png';

    const [visualPositions, setVisualPositions] = useState<{ [key: string]: number }>({});
    const [movingPieces, setMovingPieces] = useState<Set<string>>(new Set());

    useEffect(() => {
        const initial: { [key: string]: number } = {};
        players.forEach(p => {
            initial[p.namePiece] = p.position;
        });
        setVisualPositions(initial);
    }, []);

    useEffect(() => {
        players.forEach(player => {
            const current = visualPositions[player.namePiece] ?? 0;
            const target = player.position;

            if (current !== target) {
                setMovingPieces(prev => new Set(prev).add(player.namePiece));
                let step = current;

                const interval = setInterval(() => {
                    step = (step + 1) % 40;
                    setVisualPositions(prev => ({
                        ...prev,
                        [player.namePiece]: step
                    }));

                    if (step === target) {
                        clearInterval(interval);
                        setMovingPieces(prev => {
                            const updated = new Set(prev);
                            updated.delete(player.namePiece);
                            return updated;
                        });
                    }
                }, 400);
            }
        });
    }, [players]);

    const groupedByPosition: { [key: number]: Player[] } = {};
    players.forEach(player => {
        const isMoving = movingPieces.has(player.namePiece);
        const visualPos = visualPositions[player.namePiece] ?? player.position;

        if (!isMoving) {
            if (!groupedByPosition[visualPos]) {
                groupedByPosition[visualPos] = [];
            }
            groupedByPosition[visualPos].push(player);
        }
    });
    const tokensToRender: React.ReactElement[] = [];
    Object.entries(groupedByPosition).forEach(([position, group]) => {
        const baseCoords = boardPositions[Number(position)];
        const total = group.length;

        group.forEach((player, index) => {
            let offset = { x: 0, y: 0 };

            if (total === 2) offset = [{ x: -2, y: 0 }, { x: 2, y: 0 }][index];
            else if (total === 3) offset = [{ x: 0, y: -2 }, { x: -2, y: 2 }, { x: 2, y: 2 }][index];
            else if (total >= 4) {
                offset = [
                    { x: -2, y: -2 },
                    { x: 2, y: -2 },
                    { x: -2, y: 2 },
                    { x: 2, y: 2 },
                ][index] ?? { x: 0, y: 0 };
            }

            tokensToRender.push(
                <Token
                    key={player.namePiece}
                    namePiece={player.namePiece}
                    positionX={baseCoords.x + offset.x }
                    positionY={baseCoords.y + offset.y - 1}
                />
            );
        });
    });

    players.forEach(player => {
        if (movingPieces.has(player.namePiece)) {
            const visualPos = visualPositions[player.namePiece];
            const coords = boardPositions[visualPos];
            tokensToRender.push(
                <Token
                    key={player.namePiece}
                    namePiece={player.namePiece}
                    positionX={coords.x + 2}
                    positionY={coords.y + 2}
                />
            );
        }
    });

    return (
        <div className="flex items-center justify-center w-full h-full p-4">
            <div
                className="relative rounded-3xl border-4 border-yellow-400 bg-black/80 shadow-[0_0_50px_rgba(255,215,0,0.4)] overflow-hidden transition-transform duration-500 hover:scale-[1.01]"
                style={{
                    backgroundImage: `url(${boardImage})`,
                    backgroundSize: 'contain',
                    backgroundRepeat: 'no-repeat',
                    backgroundPosition: 'center',
                    width: '95vmin',
                    height: '95vmin',
                }}
            >
                {tokensToRender}

                {/* Dado en el centro */}
                <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 z-10">
                    <div className="bg-black/60 backdrop-blur-md border-2 border-white/30 rounded-full p-4 shadow-[0_0_20px_rgba(255,255,255,0.3)] animate-fade-in">
                        <Dice />
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Board;
