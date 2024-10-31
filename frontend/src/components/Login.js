import React, { useState } from 'react';
import axios from 'axios';

function Login({ onLogin, handleBack }) {
    const [loginId, setLoginId] = useState('');
    const [pw, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        setErrorMessage('');

        const dataToSend = { loginId, pw };

        try {
            const response = await axios.post('/api/v1/members/login', dataToSend);
            const { id, refreshToken, accessToken, name, mimage } = response.data; // mImage 추가

            localStorage.setItem('accessToken', accessToken); // JWT 저장
            localStorage.setItem('refreshToken', refreshToken); // JWT 저장
            localStorage.setItem('id', id); // id 저장

            onLogin( name, mimage ); // 로그인 후 사용자 이름과 이미지 전달

        } catch (error) {
            setErrorMessage('로그인 실패: ' + (error.response?.data?.message || '서버에 문제가 발생했습니다.'));
        }
    };

    return (
        <form onSubmit={handleLogin}>
            <div className="inputform">
                <div className='formInfo'>로그인</div>
            <div>
                <span className="input-info">아이디 :</span>
                <input className="auth-input" type="text" value={loginId} onChange={(e) => setLoginId(e.target.value)} placeholder="아이디를 입력하세요" required />
            </div>
            <div>
                <span className="input-info">비밀번호 :</span>
                <input className="auth-input" type="password" value={pw} onChange={(e) => setPassword(e.target.value)} placeholder="비밀번호를 입력하세요" required />
            </div>
            </div>
            {errorMessage && <div style={{ color: 'red' }}>{errorMessage}</div>}
            <div className="button-container">
                <button type="submit">로그인</button>
                <button type="button" onClick={handleBack}>뒤로가기</button>
            </div>
        </form>
    );
}

export default Login;
