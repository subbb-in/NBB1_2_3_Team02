import React from 'react';

const QnADetail = ({ question, onClose }) => {
    const userIdFromSession = 5; // 세션에서 가져온 유저 ID (예시)

    const handleEdit = () => {
        // 질문 수정 로직 구현
        console.log("수정하기");
    };

    const handleDelete = () => {
        // 질문 삭제 로직 구현
        console.log("삭제하기");
    };

    return (
        <div className="qna-modal">
            <h2>{question.title}</h2>
            <p>{question.description}</p>
            {question.userId === userIdFromSession && (
                <>
                    <button className="qna-modal-button" onClick={handleEdit}>수정</button>
                    <button className="qna-modal-button" onClick={handleDelete}>삭제</button>
                </>
            )}
            <button className="qna-modal-button" onClick={onClose}>닫기</button>
        </div>
    );
};

export default QnADetail;
