type ErrorMessageProps = {
    message: string;
  };
  
  export default function ErrorMessage({ message }: ErrorMessageProps) {
    if (!message) return null;
    
    return (
      <p className="text-red-400 mb-4">{message}</p>
    );
  }
  