import { useEffect, useState } from 'react';
import { Client} from '@stomp/stompjs';
import Cookies from 'js-cookie';
export const useMortgageWebSocket = (
    gameCode: string,
    nickName: string
) => {
    const [mortgageableProperties, setMortgageableProperties] = useState<any[]>([]);
    const [client, setClient] = useState<Client | null>(null);

    useEffect(() => {
        const stompClient = new Client({
            brokerURL: 'ws://localhost:8004/app',
            reconnectDelay: 5000,
        });
        stompClient.onConnect = () => {
            console.log("Conectado",gameCode);
            stompClient.subscribe(
                `/topic/MortgageCards/${gameCode}`,
                (message) => {
                    const data = JSON.parse(message.body);
                    console.log("Propiedades hipotecables",data);
                    setMortgageableProperties(data);
                }
            );
            stompClient.subscribe(
                `/topic/Mortgage/${gameCode}`,
                (message) => {
                    const data = JSON.parse(message.body);
                    console.log("Propiedadd hiptecada",data);
                }
            );
        };

        stompClient.activate();
        setClient(stompClient);

        return () => {
            stompClient.deactivate();
        };
    }, [gameCode, nickName]);

    const requestMortgageOptions = () => {
        const nickName = Cookies.get('nickname');
        const data = {
            codeGame: gameCode,
            nickName:  nickName
        }
        console.log("Datos para enviar",data);
        client?.publish({
            destination: `/Game/MortgageCards`,
            body: JSON.stringify(data),
        });
    };

    const sendMortgageRequest = (propertyId: number) => {
        const nickname = Cookies.get('nickname');
        const payload = {
            idCard: propertyId,
            nickName: nickname,
            codeGame: gameCode,
        };
        console.log("Datos para enviar",payload);
        client?.publish({
            destination: `/Game/Mortgage`,
            body: JSON.stringify(payload),
        });
    };

    return { mortgageableProperties, requestMortgageOptions,sendMortgageRequest };
};
