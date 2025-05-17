import { motion } from 'framer-motion';

interface TokenProps {
    positionX: number;
    positionY: number;
    namePiece: string;
}

const Token = ({ positionX, positionY, namePiece }: TokenProps) => {
    return (
        <motion.div
            className="absolute w-6 h-6 z-10"
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{
                opacity: 1,
                scale: 1,
                left: `${positionX}%`,
                top: `${positionY}%`,
            }}
            transition={{
                type: 'spring',
                stiffness: 100,  // Ajusta la rigidez para hacerlo más fluido
                damping: 30,  // Controla la desaceleración para simular el movimiento más suave
                mass: 1.5,
                duration: 1.2,  // Aumenta la duración para hacerlo más lento
                ease: "easeInOut",  // Usar una curva de aceleración y desaceleración
            }}
            style={{
                transform: 'translate(-50%, -50%)',
                filter: 'drop-shadow(0 0 6px rgba(255, 255, 255, 0.5))',
            }}
        >
            <motion.img
                src={`/Fichas/${namePiece}.png`}
                alt={namePiece}
                className="w-full h-full object-cover rounded-full"
                whileHover={{ scale: 1.1 }}
                transition={{ type: 'spring', stiffness: 200, damping: 30 }}
            />
        </motion.div>
    );
};

export default Token;