export interface PropertyModalProps {
    propiedadSeleccionada: string;
    onClose: () => void;
}

const PropertyModal = ({ propiedadSeleccionada, onClose }: PropertyModalProps) => (
    <div className="fixed inset-0 bg-black/80 flex justify-center items-center z-50">
        <div className="relative">
            <img src={propiedadSeleccionada} alt="propiedad" className="w-72 rounded shadow-lg" />
            <button
                className="absolute top-2 right-2 text-white bg-red-600 hover:bg-red-700 rounded-full px-2 py-1"
                onClick={onClose}
            >✕</button>
        </div>
    </div>
);

export default PropertyModal;
