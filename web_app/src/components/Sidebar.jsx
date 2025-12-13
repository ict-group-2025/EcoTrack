import React, { useState } from 'react';
import { Search, Globe } from 'lucide-react';
import { cities } from '../data/weatherData';

const Sidebar = ({ 
  selectedCity, 
  setSelectedCity, 
  tempUnit, 
  setTempUnit,
  searchQuery,
  setSearchQuery,
  onSearchCity,
  customCities
}) => {
  const [showWorldSearch, setShowWorldSearch] = useState(false);
  
  // Kết hợp cities mặc định và custom cities
  const allCities = [...cities, ...customCities];
  
  const filteredCities = allCities.filter(city => 
    city.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && searchQuery.trim()) {
      onSearchCity(searchQuery.trim());
    }
  };

  const handleSearchClick = () => {
    if (searchQuery.trim()) {
      onSearchCity(searchQuery.trim());
    }
  };

  return (
    <aside className="w-64 bg-teal-600/90 backdrop-blur-md p-4 min-h-[calc(100vh-140px)] border-r border-teal-500/30">
      {/* Temperature Toggle */}
      <div className="mb-4">
        <div className="flex items-center gap-2 mb-4">
          <button
            onClick={() => setTempUnit('C')}
            className={`px-3 py-1 rounded transition-colors ${
              tempUnit === 'C' ? 'bg-teal-800 text-white' : 'bg-teal-500 text-white/80 hover:bg-teal-700'
            }`}
          >
            °C
          </button>
          <div className="flex-1 h-2 bg-teal-500 rounded-full relative">
            <div 
              className="absolute top-1/2 -translate-y-1/2 w-4 h-4 bg-white rounded-full shadow-lg transition-all duration-300"
              style={{ left: tempUnit === 'C' ? '10%' : '90%', transform: 'translate(-50%, -50%)' }}
            />
          </div>
          <button
            onClick={() => setTempUnit('F')}
            className={`px-3 py-1 rounded transition-colors ${
              tempUnit === 'F' ? 'bg-teal-800 text-white' : 'bg-teal-500 text-white/80 hover:bg-teal-700'
            }`}
          >
            °F
          </button>
        </div>
      </div>

      {/* World Search Toggle */}
      <div className="mb-4">
        <button
          onClick={() => setShowWorldSearch(!showWorldSearch)}
          className={`w-full flex items-center justify-center gap-2 px-4 py-2 rounded-lg transition-all ${
            showWorldSearch 
              ? 'bg-blue-600 text-white' 
              : 'bg-teal-500/50 text-white/80 hover:bg-teal-500'
          }`}
        >
          <Globe className="w-4 h-4" />
          <span className="text-sm font-semibold">
            {showWorldSearch ? 'Tìm kiếm toàn cầu' : 'Mở rộng tìm kiếm'}
          </span>
        </button>
      </div>

      {/* Search Box */}
      <div className="relative mb-4">
        <input
          type="text"
          placeholder={showWorldSearch ? "Tìm thành phố (toàn cầu)..." : "Tìm trong danh sách..."}
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          onKeyPress={handleKeyPress}
          className="w-full px-4 py-2 pr-20 rounded-lg bg-white/20 text-white placeholder-white/60 focus:outline-none focus:ring-2 focus:ring-white/40 transition-all"
        />
        <div className="absolute right-2 top-1/2 -translate-y-1/2 flex gap-1">
          {showWorldSearch && searchQuery && (
            <button
              onClick={handleSearchClick}
              className="p-1.5 bg-blue-600 hover:bg-blue-700 rounded text-white transition-colors"
              title="Tìm kiếm"
            >
              <Search className="w-4 h-4" />
            </button>
          )}
          {searchQuery && (
            <button
              onClick={() => setSearchQuery('')}
              className="p-1.5 text-white/60 hover:text-white hover:bg-white/10 rounded transition-colors"
              title="Xóa"
            >
              ✕
            </button>
          )}
        </div>
      </div>

      {/* Instructions for World Search */}
      {showWorldSearch && (
        <div className="mb-4 p-3 bg-blue-600/30 rounded-lg border border-blue-400/50">
          <p className="text-white/90 text-xs">
            💡 <strong>Gợi ý:</strong> Nhập tên thành phố bằng tiếng Anh
            (VD: Tokyo, Paris, New York) và nhấn Enter hoặc nút 🔍
          </p>
        </div>
      )}

      {/* City List Header */}
      <div className="mb-2 text-white/80 text-sm font-semibold flex items-center justify-between">
        <span>
          {showWorldSearch ? '🌍 Thành phố đã tìm' : '🇻🇳 Việt Nam'}
        </span>
        <span className="text-xs">({allCities.length})</span>
      </div>

      {/* Cities List */}
      <div className="space-y-2 max-h-[calc(100vh-450px)] overflow-y-auto custom-scrollbar">
        {filteredCities.length > 0 ? (
          <>
            {/* Default Vietnam Cities */}
            {!showWorldSearch && filteredCities.filter(city => cities.includes(city)).map(city => (
              <button
                key={city}
                onClick={() => {
                  setSelectedCity(city);
                  setSearchQuery('');
                }}
                className={`w-full text-left px-4 py-3 rounded-lg transition-all duration-200 ${
                  selectedCity === city
                    ? 'bg-teal-800 text-white font-semibold shadow-lg scale-105'
                    : 'bg-teal-500/50 text-white/80 hover:bg-teal-500 hover:scale-102'
                }`}
              >
                <div className="flex items-center justify-between">
                  <span>{city}</span>
                  {selectedCity === city && <span className="text-sm">✓</span>}
                </div>
              </button>
            ))}
            
            {/* Custom Cities */}
            {customCities.length > 0 && (
              <>
                <div className="my-3 pt-3 border-t border-teal-400/30">
                  <p className="text-white/60 text-xs mb-2">🌍 Đã tìm kiếm:</p>
                </div>
                {filteredCities.filter(city => customCities.includes(city)).map(city => (
                  <button
                    key={city}
                    onClick={() => {
                      setSelectedCity(city);
                      setSearchQuery('');
                    }}
                    className={`w-full text-left px-4 py-3 rounded-lg transition-all duration-200 ${
                      selectedCity === city
                        ? 'bg-blue-700 text-white font-semibold shadow-lg scale-105'
                        : 'bg-blue-500/50 text-white/80 hover:bg-blue-500 hover:scale-102'
                    }`}
                  >
                    <div className="flex items-center justify-between">
                      <span>{city}</span>
                      {selectedCity === city && <span className="text-sm">✓</span>}
                    </div>
                  </button>
                ))}
              </>
            )}
          </>
        ) : (
          <div className="text-white/60 text-center py-4 text-sm">
            {showWorldSearch ? 'Nhập tên thành phố và nhấn Enter' : 'Không tìm thấy thành phố'}
          </div>
        )}
      </div>

      {/* Footer Info */}
      <div className="mt-6 pt-4 border-t border-teal-500/30">
        <div className="text-white/60 text-xs text-center space-y-1">
          <p>📍 {allCities.length} thành phố</p>
          <p className="text-white/40">OpenWeatherMap</p>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;