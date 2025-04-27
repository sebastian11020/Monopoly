type JoinButtonProps = {
    onClick: () => void;
  };
  
  export default function JoinButton({ onClick }: JoinButtonProps) {
    return (
      <button
        onClick={onClick}
        className="w-full bg-green-500 hover:bg-green-600 text-white font-bold py-2 rounded transition"
      >
        Ingresar
      </button>
    );
  }
  