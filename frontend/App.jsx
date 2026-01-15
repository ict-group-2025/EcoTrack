import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import NewHomePage from './components/NewHomePage';
import UnifiedCityPage from './components/UnifiedCityPage';
import LoginPage from './components/LoginPage';
import CommunityLobby from './components/CommunityLobby';
import ChatRoom from './components/ChatRoom';
import ScrollToTop from './components/ScrollToTop';
import CommunityWeatherChat from './components/CommunityWeatherChat';
import AQIPage from './components/AQIPage';

// ✅ NEW: Trang chi tiết tin tức (đúng theo cấu trúc dự án của bạn)
import NewsDetail from './components/NewsDetail';

// Protected Route Component
const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();
  
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-900">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-purple-500"></div>
      </div>
    );
  }
  
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: window.location }} replace />;
  }
  
  return children;
};

function AppRoutes() {
  const [tempUnit, setTempUnit] = useState('C');

  return (
    <BrowserRouter>
      <ScrollToTop />

      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<NewHomePage tempUnit={tempUnit} />} />
        <Route path="/city/:cityName" element={<UnifiedCityPage />} />
        <Route path="/login" element={<LoginPage />} />

        {/* ✅ NEW: News Detail Route (Public) */}
        <Route path="/news/:id" element={<NewsDetail />} />

        {/* Protected Routes - Cần đăng nhập */}
        <Route 
          path="/community" 
          element={
            <ProtectedRoute>
              <CommunityLobby />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/community/room/:citySlug" 
          element={
            <ProtectedRoute>
              <ChatRoom />
            </ProtectedRoute>
          } 
        />
      </Routes>

      {/* Floating Chat Bubble - Hiển thị trên mọi trang */}
      <CommunityWeatherChat city="Hanoi" />
    </BrowserRouter>
  );
}

function App() {
  return (
    <AuthProvider>
      <AppRoutes />
    </AuthProvider>
  );
}

export default App;
