import React, { useState, useEffect } from 'react';
import QnADetail from './QnADetail';
import axiosInstance from "../axiosInstance"; // 질문 상세보기 컴포넌트

const QnAPage = () => {
    const [questions, setQuestions] = useState([]);
    const [newQuestion, setNewQuestion] = useState({ title: '', description: '' });
    const [editingQuestion, setEditingQuestion] = useState(null);
    const [errorMessage, setErrorMessage] = useState('');
    const [showDetail, setShowDetail] = useState(null);

    // 질문 목록 가져오기
    useEffect(() => {
        const fetchQuestions = async () => {
            try {
                const response = await axiosInstance.get('/api/v1/qna/all');
                setQuestions(response.data.questions);
            } catch (error) {
                setErrorMessage('질문을 가져오는 데 실패했습니다.');
            }
        };

        fetchQuestions();
    }, []);

    // 질문 추가
    const handleAddQuestion = async (e) => {
        e.preventDefault();
        try {
            const response = await axiosInstance.post('/api/v1/qna/post', newQuestion);
            setQuestions([...questions, response.data]);
            setNewQuestion({ title: '', description: '' });
        } catch (error) {
            setErrorMessage('질문 추가에 실패했습니다.');
        }
    };

    // 질문 수정
    const handleEditQuestion = async (question) => {
        try {
            const response = await axiosInstance.put(`/api/v1/qna/${question.id}`, question);
            setQuestions(questions.map(q => (q.id === question.id ? response.data : q)));
            setEditingQuestion(null);
        } catch (error) {
            setErrorMessage('질문 수정에 실패했습니다.');
        }
    };

    // 질문 삭제
    const handleDeleteQuestion = async (id) => {
        try {
            await axiosInstance.delete(`/api/v1/qna/${id}`);
            setQuestions(questions.filter(q => q.id !== id));
        } catch (error) {
            setErrorMessage('질문 삭제에 실패했습니다.');
        }
    };

    return (
        <div>
            <h1>QnA 게시판</h1>
            {errorMessage && <div className="error">{errorMessage}</div>}

            <button onClick={() => setEditingQuestion({ title: '', description: '' })}>
                질문 게시
            </button>

            {editingQuestion && (
                <form onSubmit={(e) => handleEditQuestion(editingQuestion)}>
                    <input
                        type="text"
                        placeholder="제목"
                        value={editingQuestion.title}
                        onChange={(e) => setEditingQuestion({ ...editingQuestion, title: e.target.value })}
                        required
                    />
                    <textarea
                        placeholder="설명"
                        value={editingQuestion.description}
                        onChange={(e) => setEditingQuestion({ ...editingQuestion, description: e.target.value })}
                        required
                    />
                    <button type="submit">수정하기</button>
                    <button type="button" onClick={() => setEditingQuestion(null)}>취소</button>
                </form>
            )}

            <h2>질문 목록</h2>
            <ul>
                {questions.map(question => (
                    <li key={question.id}>
                        <h3 onClick={() => setShowDetail(question)}>{question.title}</h3>
                        <p>{question.description}</p>
                        <button onClick={() => setEditingQuestion(question)}>수정</button>
                        <button onClick={() => handleDeleteQuestion(question.id)}>삭제</button>
                    </li>
                ))}
            </ul>

            {showDetail && (
                <QnADetail question={showDetail} onClose={() => setShowDetail(null)} />
            )}
        </div>
    );
};

export default QnAPage;
