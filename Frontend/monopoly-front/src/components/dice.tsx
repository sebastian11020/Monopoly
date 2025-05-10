import { useEffect, useState } from 'react';

interface DiceProps {
    value1: number;
    value2: number;
    triggerRoll: boolean; // Cambia cuando se presiona espacio
}

const Dice = ({ value1, value2, triggerRoll }: DiceProps) => {
    const diceImages = [
        '/assets/Dado1.png',
        '/assets/Dado2.png',
        '/assets/Dado3.png',
        '/assets/Dado4.png',
        '/assets/Dado5.png',
        '/assets/Dado6.png',
    ];

    const [rolling, setRolling] = useState(false);
    const [currentDice, setCurrentDice] = useState<[number, number]>([value1, value2]);

    useEffect(() => {
        if (triggerRoll) {
            setRolling(true);
            let count = 0;
            const interval = setInterval(() => {
                const rand1 = Math.floor(Math.random() * 6) + 1;
                const rand2 = Math.floor(Math.random() * 6) + 1;
                setCurrentDice([rand1, rand2]);
                count++;
                if (count > 10) {
                    clearInterval(interval);
                    setCurrentDice([value1, value2]);
                    setRolling(false);
                }
            }, 70);
        }
    }, [triggerRoll, value1, value2]);

    const getImage = (val: number) => diceImages[val - 1];

    return (
        <div className={`flex gap-2 items-center justify-center ${rolling ? 'animate-pulse' : ''}`}>
            <img src={getImage(currentDice[0])} alt={`Dado ${currentDice[0]}`} className="w-14 h-14" />
            <img src={getImage(currentDice[1])} alt={`Dado ${currentDice[1]}`} className="w-14 h-14" />
        </div>
    );
};

export default Dice;
