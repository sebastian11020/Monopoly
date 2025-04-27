import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import WaitingRoom from './pages/waitingRoom'; // Asegúrate de que el nombre del archivo y componente estén correctos
import CodePage from './pages/pageCode'; // Asegúrate de que el componente esté exportado correctamente

const App = () => {
    return (
        <Router>
            <Routes>
                <Route path="/waiting-room" element={<WaitingRoom />} />
                <Route path="/page-code" element={<CodePage />} />
            </Routes>
        </Router>
    );
};

export default App;
