import { useState } from 'react';

type CodeInputProps = {
    onSubmit: (code: string) => void;
};

export default function CodeInput({ onSubmit }: CodeInputProps) {
    const [code, setCode] = useState('');

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(code);
    };

    return (
        <form onSubmit={handleSubmit} className="flex flex-col items-center space-y-4">
            <input
                type="text"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                placeholder="Código de sala"
                className="p-3 rounded-lg text-black text-lg"
            />
            <button
                type="submit"
                className="px-6 py-3 bg-green-500 hover:bg-green-600 rounded-full text-white font-bold shadow-lg transition-all"
            >
                Ingresar
            </button>
        </form>
    );
}
