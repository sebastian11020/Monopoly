import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import WaitingRoom from './pages/waitingRoom';
import CodePage from './pages/pagecode.tsx';
import WaitingRoomJoin from './pages/waitingRoomJoin';
import Register from './pages/Register';
import Login from './pages/Login';
import Menu from './pages/Menu';

function App() {
    return (
    <Router>
        <Routes>
            <Route path="/" element={<Login/>}/>
            <Route path="/register" element={<Register/>}/>
            <Route path="/menu" element={<Menu/>}/>
            <Route path="/waiting-room-create" element={<WaitingRoom/>} />
            <Route path="/waiting-room-join" element={<WaitingRoomJoin/>}/>
            <Route path="/page-code" element={<CodePage />} />
        </Routes>
    </Router>
);
}

export default App;
