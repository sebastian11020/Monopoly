interface MoneyDisplayProps {
    cash: number;
}

const BILLETES = [
    { valor: 100, imagen: '/assets/Billete-100.png' },
    { valor: 50, imagen: '/assets/Billete-50.png' },
    { valor: 20, imagen: '/assets/Billete-20.png' },
    { valor: 10, imagen: '/assets/Billete-10.png' },
    { valor: 5, imagen: '/assets/Billete-5.png' },
    { valor: 1, imagen: '/assets/Billete-1.png' },
];

function calcularBilletes(cantidad: number) {
    const resultado: { imagen: string; cantidad: number }[] = [];
    for (const billete of BILLETES) {
        const cantidadBilletes = Math.floor(cantidad / billete.valor);
        if (cantidadBilletes > 0) {
            resultado.push({ imagen: billete.imagen, cantidad: cantidadBilletes });
            cantidad -= cantidadBilletes * billete.valor;
        }
    }
    return resultado;
}

const MoneyDisplay = ({ cash }: MoneyDisplayProps) => {
    const billetes = calcularBilletes(cash);

    return (
        <div className="flex items-center gap-3 overflow-x-auto">
            {billetes.map((b, i) => (
                <div key={i} className="flex items-center gap-1">
                    <img src={b.imagen} alt={`billete${i}`} className="w-14 h-auto drop-shadow-md" />
                    <span className="text-sm text-white">x{b.cantidad}</span>
                </div>
            ))}
            <span className="text-2xl font-extrabold text-green-400 ml-2">${cash}</span>
        </div>
    );
};

export default MoneyDisplay;
