import { useEffect, useState, useRef } from 'react';

interface DiceProps {
    value1: number;
    value2: number;
    triggerRoll: boolean;
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

    const rollSound = useRef<HTMLAudioElement | null>(null);

    useEffect(() => {
        if (triggerRoll) {
            // Reproducir sonido de dados
            rollSound.current?.play();

            setRolling(true);
            const finalValue1 = value1;
            const finalValue2 = value2;

            let count = 0;
            const interval = setInterval(() => {
                const rand1 = Math.floor(Math.random() * 6) + 1;
                const rand2 = Math.floor(Math.random() * 6) + 1;
                setCurrentDice([rand1, rand2]);
                count++;
                if (count > 10) {
                    clearInterval(interval);
                    setCurrentDice([finalValue1, finalValue2]);
                    setRolling(false);
                }
            }, 70);
        }
    }, [triggerRoll]);

    const getImage = (val: number) => diceImages[val - 1];

    return (
        <div className="relative">
            <audio ref={rollSound} src="/sounds/tirar-dados.mp3" preload="auto" />

            <div
                className={`flex gap-2 items-center justify-center transition-transform duration-200 ${
                    rolling ? 'animate-roll-dice' : ''
                }`}
            >
                <img
                    src={getImage(currentDice[0])}
                    alt={`Dado ${currentDice[0]}`}
                    className={`w-14 h-14 ${rolling ? 'animate-dice-shake' : ''}`}
                />
                <img
                    src={getImage(currentDice[1])}
                    alt={`Dado ${currentDice[1]}`}
                    className={`w-14 h-14 ${rolling ? 'animate-dice-shake' : ''}`}
                />
            </div>
        </div>
    );
};

export default Dice;
