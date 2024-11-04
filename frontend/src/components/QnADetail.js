import React from 'react';

const QnADetail = ({ question, onClose }) => {
    return (
        <div className="modal">
            <h2>{question.title}</h2>
            <p>{question.description}</p>
            <button onClick={onClose}>닫기</button>
        </div>
    );
};

export default QnADetail;
