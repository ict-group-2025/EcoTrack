import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { Check, X } from 'lucide-react';

// Avatar metadata
const AVATARS = [
    { id: 1, name: 'Seedling', emoji: '🌱' },
    { id: 2, name: 'Tree', emoji: '🌳' },
    { id: 3, name: 'Sun', emoji: '☀️' },
    { id: 4, name: 'Moon', emoji: '🌙' },
    { id: 5, name: 'Cloud', emoji: '⛅' },
    { id: 6, name: 'Wave', emoji: '🌊' },
    { id: 7, name: 'Flower', emoji: '🌸' },
    { id: 8, name: 'Butterfly', emoji: '🦋' },
    { id: 9, name: 'Bear', emoji: '🐻' },
    { id: 10, name: 'Rainbow', emoji: '🌈' },
];

// Helper function to get avatar URL
export const getAvatarUrl = (avatarId) => {
    const id = avatarId || 1;
    return `http://localhost:8080/avatars/avatar-${id}.png`;
};

const AvatarSelector = ({ isOpen, onClose }) => {
    const { user, updateProfile } = useAuth();
    const [selectedId, setSelectedId] = useState(user?.avatarId || 1);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    if (!isOpen) return null;

    const handleSelect = (avatarId) => {
        setSelectedId(avatarId);
    };

    const handleSave = async () => {
        setLoading(true);
        setError('');

        try {
            const response = await fetch('http://localhost:8080/auth/avatar', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('auth_token')}`
                },
                body: JSON.stringify({ avatarId: selectedId })
            });

            if (response.ok) {
                await updateProfile({ avatarId: selectedId });
                onClose();
            } else {
                const data = await response.json();
                setError(data.message || 'Không thể cập nhật avatar');
            }
        } catch (err) {
            setError('Lỗi kết nối server');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
            <div className="bg-slate-800 rounded-2xl p-6 w-full max-w-md border border-slate-700 shadow-2xl">
                <div className="flex items-center justify-between mb-6">
                    <h2 className="text-xl font-bold text-white">Chọn Avatar</h2>
                    <button onClick={onClose} className="p-2 hover:bg-slate-700 rounded-lg transition text-slate-400 hover:text-white">
                        <X size={20} />
                    </button>
                </div>

                {error && (
                    <div className="mb-4 p-3 bg-red-500/20 border border-red-500/30 rounded-lg text-red-400 text-sm">{error}</div>
                )}

                <div className="grid grid-cols-5 gap-3 mb-6">
                    {AVATARS.map((avatar) => (
                        <button
                            key={avatar.id}
                            onClick={() => handleSelect(avatar.id)}
                            className={`relative aspect-square rounded-xl overflow-hidden border-2 transition-all hover:scale-105 ${selectedId === avatar.id ? 'border-emerald-500 ring-2 ring-emerald-500/30' : 'border-transparent hover:border-slate-600'
                                }`}
                            title={avatar.name}
                        >
                            <img src={getAvatarUrl(avatar.id)} alt={avatar.name} className="w-full h-full object-cover" />
                            {selectedId === avatar.id && (
                                <div className="absolute inset-0 bg-emerald-500/20 flex items-center justify-center">
                                    <Check className="text-emerald-400" size={24} />
                                </div>
                            )}
                        </button>
                    ))}
                </div>

                <div className="flex gap-3">
                    <button onClick={onClose} className="flex-1 py-3 px-4 rounded-xl border border-slate-600 text-slate-300 hover:bg-slate-700 transition font-medium">
                        Hủy
                    </button>
                    <button
                        onClick={handleSave}
                        disabled={loading || selectedId === user?.avatarId}
                        className="flex-1 py-3 px-4 rounded-xl bg-emerald-500 hover:bg-emerald-600 text-white font-medium transition disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {loading ? 'Đang lưu...' : 'Lưu thay đổi'}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default AvatarSelector;
export { AVATARS };
