import { useEffect, useState } from 'react';
import { Client} from '@stomp/stompjs';
import Cookies from 'js-cookie';
export const UseBuildWebSocket = (
    gameCode: string,
    nickName: string
) => {
    const [buildableProperties, setBuildableProperties] = useState<any[]>([]);
    const [client, setClient] = useState<Client | null>(null);

    useEffect(() => {
        const stompClient = new Client({
            brokerURL: import.meta.env.VITE_WS_URL,
            reconnectDelay: 5000,
        });
        stompClient.onConnect = () => {
            console.log("Conectado",gameCode);
            stompClient.subscribe(
                `/topic/CardsBuild/${gameCode}`,
                (message) => {
                    const data = JSON.parse(message.body);
                    console.log("Propiedades para construir",data);
                    setBuildableProperties(data);
                }
            );
            stompClient.subscribe(
                `/topic/BuildProperty/${gameCode}`,
                (message) => {
                    const data = JSON.parse(message.body);
                    console.log("Propiedadd Construida",data);
                }
            );
        };

        stompClient.activate();
        setClient(stompClient);

        return () => {
            stompClient.deactivate();
        };
    }, [gameCode, nickName]);

    const requestBuildOptions = () => {
        const nickName = Cookies.get('nickname');
        const data = {
            codeGame: gameCode,
            nickName:  nickName
        }
        console.log("Datos para enviar",data);
        client?.publish({
            destination: `/Game/CardsBuild`,
            body: JSON.stringify(data),
        });
    };

    const sendBuildRequest = (propertyId: number) => {
        const nickname = Cookies.get('nickname');
        const payload = {
            idCard: propertyId,
            nickName: nickname,
            codeGame: gameCode,
        };
        console.log("Datos para enviar",payload);
        client?.publish({
            destination: `/Game/BuildProperty`,
            body: JSON.stringify(payload),
        });
    };

    return { buildableProperties, requestBuildOptions,sendBuildRequest };
};
