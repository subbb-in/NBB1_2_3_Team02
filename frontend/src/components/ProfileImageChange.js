import React, { useState } from 'react';
import axiosInstance from "../axiosInstance";

function ProfileImageChange({ userId, onProfileImageChange, onSuccess, onError }) {
    const [file, setFile] = useState(null);

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData();
        if (file) {
            formData.append('mImage', file);
        }

        try {
            const response = await axiosInstance.put(`/api/v1/members/update-image`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });

            if (response.status === 200) {
                const updatedImageUrl = `/api/v1/members/upload/${file.name}`;
                onProfileImageChange(updatedImageUrl); // 프로필 이미지 URL을 부모에게 전달
                onSuccess('프로필 이미지가 업데이트 되었습니다.');
            } else {
                onError('프로필 이미지 업데이트에 실패했습니다.');
            }
        } catch (error) {
            console.error('프로필 이미지 업데이트 에러:', error);
            onError('서버에 문제가 발생했습니다.');
        }
    };

    return (
        <form onSubmit={handleSubmit}>
            <div>
                <label>새 프로필 이미지:</label>
                <input type="file" onChange={handleFileChange} />
            </div>
            <button type="submit">업데이트</button>
        </form>
    );
}

export default ProfileImageChange;
