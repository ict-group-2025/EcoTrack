import React, { useState, useEffect } from 'react';
import Header from './components/Header';
import Navigation from './components/Navigation';
import Sidebar from './components/Sidebar';
import HomePage from './components/HomePage';
import LiveDataPage from './components/LiveDataPage';
import AQIPage from './components/AQIPage';
import { getAllWeatherData } from './services/weatherService';
import { defaultWeatherData } from './data/weatherData';

function App() {
  const [activeTab, setActiveTab] = useState('home');
  const [tempUnit, setTempUnit] = useState('C');
  const [selectedCity, setSelectedCity] = useState('Hanoi');
  const [searchQuery, setSearchQuery] = useState('');
  const [weatherData, setWeatherData] = useState(defaultWeatherData);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [allCitiesData, setAllCitiesData] = useState({});
  const [customCities, setCustomCities] = useState([]); // Các thành phố tự search

  // Load dữ liệu thời tiết khi chọn thành phố
  useEffect(() => {
    const fetchWeatherData = async () => {
      setLoading(true);
      setError(null);
      
      try {
        const data = await getAllWeatherData(selectedCity);
        setWeatherData(data);
        
        // Lưu vào cache
        setAllCitiesData(prev => ({
          ...prev,
          [selectedCity]: data
        }));
      } catch (err) {
        setError('Không thể tải dữ liệu thời tiết. Vui lòng thử lại.');
        console.error('Error:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchWeatherData();
    
    // Refresh data every 5 minutes
    const interval = setInterval(fetchWeatherData, 5 * 60 * 1000);
    
    return () => clearInterval(interval);
  }, [selectedCity]);

  // Xử lý search thành phố mới
  const handleSearchCity = async (cityName) => {
    if (!cityName.trim()) return;
    
    setLoading(true);
    setError(null);
    
    try {
      const data = await getAllWeatherData(cityName);
      
      // Thêm vào danh sách custom cities nếu chưa có
      if (!customCities.includes(cityName)) {
        setCustomCities(prev => [...prev, cityName]);
      }
      
      // Set làm thành phố được chọn
      setSelectedCity(cityName);
      setWeatherData(data);
      
      // Lưu cache
      setAllCitiesData(prev => ({
        ...prev,
        [cityName]: data
      }));
      
      setSearchQuery('');
    } catch (err) {
      setError(`Không tìm thấy thành phố "${cityName}". Vui lòng kiểm tra lại tên thành phố.`);
      setTimeout(() => setError(null), 5000);
    } finally {
      setLoading(false);
    }
  };

  const data = weatherData;
  const dataWithCity = { ...data, city: selectedCity };

  const renderContent = () => {
    if (loading && Object.keys(allCitiesData).length === 0) {
      return (
        <div className="flex-1 flex items-center justify-center">
          <div className="text-center">
            <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-purple-500 mx-auto mb-4"></div>
            <p className="text-white text-xl">Đang tải dữ liệu thời tiết...</p>
          </div>
        </div>
      );
    }

    if (error && Object.keys(allCitiesData).length === 0) {
      return (
        <div className="flex-1 flex items-center justify-center">
          <div className="text-center text-white">
            <div className="text-6xl mb-4">⚠️</div>
            <p className="text-xl mb-4">{error}</p>
            <button 
              onClick={() => window.location.reload()}
              className="bg-purple-500 hover:bg-purple-600 px-6 py-3 rounded-lg"
            >
              Thử lại
            </button>
          </div>
        </div>
      );
    }

    switch(activeTab) {
      case 'home':
        return <HomePage data={dataWithCity} tempUnit={tempUnit} loading={loading} />;
      case 'live data':
        return <LiveDataPage tempUnit={tempUnit} allCitiesData={allCitiesData} />;
      case 'about':
        return <AQIPage data={data} selectedCity={selectedCity} allCitiesData={allCitiesData} />;
      case 'prediction':
        return (
          <div className="flex-1 p-6">
            <div className="max-w-7xl mx-auto text-center text-white/60 mt-20">
              <h2 className="text-2xl mb-4">Weather Prediction</h2>
              <p>7-day forecast coming soon...</p>
              <p className="text-sm mt-2">Tính năng này cần upgrade OpenWeather API plan</p>
            </div>
          </div>
        );
      case 'historytrends':
        return (
          <div className="flex-1 p-6">
            <div className="max-w-7xl mx-auto text-center text-white/60 mt-20">
              <h2 className="text-2xl mb-4">Historical Data & Trends</h2>
              <p>Historical analysis coming soon...</p>
              <p className="text-sm mt-2">Tính năng này cần upgrade OpenWeather API plan</p>
            </div>
          </div>
        );
      default:
        return <HomePage data={dataWithCity} tempUnit={tempUnit} loading={loading} />;
    }
  };

  return (
    <div className="min-h-screen bg-gray-900 text-white relative overflow-hidden">
      {/* Background Image with Blur */}
      <div 
        className="fixed inset-0 z-0"
        style={{
          backgroundImage: 'url(src/assets/background.jpg)',
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
          filter: 'blur(8px)',
          transform: 'scale(1.1)' // Để tránh viền trắng khi blur
        }}
      />
      
      {/* Dark Overlay */}
      <div className="fixed inset-0 z-0 bg-gray-900/70" />
      
      {/* Content */}
      <div className="relative z-10">
        <Header />
        <Navigation activeTab={activeTab} setActiveTab={setActiveTab} />
        
        {error && activeTab === 'home' && (
          <div className="max-w-7xl mx-auto px-4 mt-4">
            <div className="bg-red-500/20 border border-red-500 text-white px-4 py-3 rounded-lg flex items-center gap-3">
              <span>⚠️</span>
              <span>{error}</span>
            </div>
          </div>
        )}
        
        <div className="flex">
          <Sidebar 
            selectedCity={selectedCity}
            setSelectedCity={setSelectedCity}
            tempUnit={tempUnit}
            setTempUnit={setTempUnit}
            searchQuery={searchQuery}
            setSearchQuery={setSearchQuery}
            onSearchCity={handleSearchCity}
            customCities={customCities}
          />
          {renderContent()}
        </div>
      </div>
    </div>
  );
}

export default App;