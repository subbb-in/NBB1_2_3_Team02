import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import './ChatComponent.css';

const ChatComponent = ({ user }) => {
    const [client, setClient] = useState(null);
    const [sendMessages, setSendMessages] = useState([]); // 사용자에게 보낸 메시지들
    const [inputMessage, setInputMessage] = useState(''); // 입력 중인 메시지

    useEffect(() => {
        // STOMP 클라이언트 설정
        const stompClient = new Client({
            webSocketFactory: () => {
                return new SockJS('http://localhost:8090/ws'); // 웹소켓 엔드포인트
            },
            debug: (str) => {
                console.log(str);
            },
            onConnect: () => {
                console.log('Connected');
                stompClient.subscribe(`/topic/public`, (message) => {
                    if (message.body) {
                        setSendMessages((prevMessages) => [...prevMessages, JSON.parse(message.body)]);
                    }
                });
            },
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            }
        });

        setClient(stompClient);
        stompClient.activate(); // 웹소켓 연결 시작

        // 컴포넌트 언마운트 시 연결 종료
        return () => {
            stompClient.deactivate();
        };
    }, [user]);

    const handleSendMessage = (e) => {
        if (client && inputMessage) {
            e.preventDefault();

            const chatMessageAd = {
                type: 'CHAT',
                content: inputMessage,
                sender: user,  // 보내는 사람은 사용자
                receiver: 'public'  // 받는 사람은 어드민
            };
            client.publish({ destination: '/app/chat.sendMessage', body: JSON.stringify(chatMessageAd) });

            setInputMessage(''); // 메시지 전송 후 입력창 초기화
        }
    };

    return (
        <div className="chat-container">
            <h2>실시간 채팅 상담</h2>
            <div className="chat-message-container">
                {sendMessages.map((msg, index) => (
                    <div className="chat-message" key={index}>
                        <strong>{msg.sender}: </strong>{msg.content}
                    </div>
                ))}
            </div>
            <form>
                <input
                    className="chat-input"
                    type="text"
                    value={inputMessage}
                    onChange={(e) => setInputMessage(e.target.value)}
                    placeholder="메시지를 입력하세요"
                />
                <button className="chat-button" onClick={handleSendMessage}>전송</button>
            </form>
        </div>
    );
};

export default ChatComponent;
