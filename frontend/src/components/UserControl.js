import React, { useEffect, useState } from 'react';
import axiosInstance from "../axiosInstance";

function UserControl() {
    const [users, setUsers] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    // 제품 목록을 가져오는 함수
    const fetchUserControl = async (page) => {
        try {
            const response = await axiosInstance.get(`/api/adm/members/all`, {
                params: { page },
            });

            console.log('Requested Page:', page); // 요청한 페이지 로그
            console.log('API Response:', response.data); // 응답 로그

            setUsers(response.data.content); // 실제 제품 목록
            setTotalPages(response.data.totalPages); // 전체 페이지 수
        } catch (error) {
            alert('사용자 목록을 불러오는 데 실패했습니다: ' + (error.response?.data?.message || '알 수 없는 오류'));
        }
    };

    useEffect(() => {
        console.log(`Fetching userControl for page: ${currentPage}`); // 현재 페이지 로그
        fetchUserControl(currentPage); // fetchProducts 함수 호출
    }, [currentPage]); // currentPage가 바뀔 때마다 실행


    return (
        <div>
            <h2>사용자 목록</h2>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                <tr>
                    <th style={{ width: '25%', padding: '8px', textAlign: 'left' }}>회원명</th>
                    <th style={{ width: '25%', padding: '8px', textAlign: 'left' }}>가입 ID</th>
                </tr>
                </thead>
                <tbody>
                {users.length > 0 ? (
                    users.map((user, index) => (
                        <tr key={index}>
                            <td style={{ padding: '8px', border: '1px solid #ccc' }}>{user.name}</td>
                            <td style={{ padding: '8px', border: '1px solid #ccc' }}>{user.loginId}</td>
                        </tr>
                    ))
                ) : (
                    <tr>
                        <td colSpan="2" style={{ textAlign: 'center', padding: '8px' }}>사용자가 없습니다.</td>
                    </tr>
                )}
                </tbody>
            </table>
            <div style={{ marginTop: '16px' }}>
                <button
                    onClick={() => setCurrentPage(currentPage - 1)}
                    disabled={currentPage <= 0}
                >
                    이전
                </button>
                <span> 페이지 {currentPage + 1} / {totalPages} </span>
                <button
                    onClick={() => setCurrentPage(currentPage + 1)}
                    disabled={currentPage >= totalPages - 1}
                >
                    다음
                </button>
            </div>
        </div>
    );
}

export default UserControl;
