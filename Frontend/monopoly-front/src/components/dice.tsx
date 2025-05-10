import { useEffect, useState } from 'react';

const Dice = () => {
    const diceFaces = [
        '/assets/Dado1.png',
        '/assets/Dado2.png',
        '/assets/Dado3.png',
        '/assets/Dado4.png',
        '/assets/Dado5.png',
        '/assets/Dado6.png',
    ];

    const [dice1, setDice1] = useState(0);
    const [dice2, setDice2] = useState(0);
    const [rolling, setRolling] = useState(false);

    useEffect(() => {
            const handleKeyDown = (e:any) => {
            if (e.code === 'Space' && !rolling) {
                rollDice();
            }
        };

        window.addEventListener('keydown', handleKeyDown);
        return () => window.removeEventListener('keydown', handleKeyDown);
    }, [rolling]);

    const rollDice = () => {
        setRolling(true);
        let count = 0;
        const interval = setInterval(() => {
            setDice1(Math.floor(Math.random() * 6));
            setDice2(Math.floor(Math.random() * 6));
            count++;
            if (count > 10) {
                clearInterval(interval);
                setRolling(false);
            }
        }, 100);
    };

    return (
        <div className="flex gap-4">
            <img src={diceFaces[dice1]} alt="Dado 1" className="w-16 h-16" />
            <img src={diceFaces[dice2]} alt="Dado 2" className="w-16 h-16" />
        </div>
    );
};

export default Dice;
