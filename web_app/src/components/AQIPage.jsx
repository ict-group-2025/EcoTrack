import React, { useState, useEffect } from 'react';
import { cities } from '../data/weatherData';
import { getAQIColor, getAQILevel, getAQIDescription } from '../utils/helpers';

const AQIPage = ({ data, selectedCity, allCitiesData }) => {
  const [citiesAQI, setCitiesAQI] = useState({});

  useEffect(() => {
    // Lấy AQI từ cache
    const aqiData = {};
    cities.forEach(city => {
      if (allCitiesData[city]) {
        aqiData[city] = allCitiesData[city].aqi || 0;
      }
    });
    setCitiesAQI(aqiData);
  }, [allCitiesData]);

  return (
    <div className="flex-1 p-6">
      <div className="max-w-7xl mx-auto">
        <h2 className="text-white text-3xl font-bold mb-6">Air Quality Index (AQI)</h2>
        
        <div className="bg-gray-800 rounded-2xl p-6 mb-6 shadow-xl">
          <h3 className="text-white text-xl font-bold mb-4">Current AQI: {selectedCity}</h3>
          <div className="flex items-center gap-6">
            <div className={`text-6xl font-bold text-white p-8 rounded-2xl ${getAQIColor(data.aqi || 0)}`}>
              {data.aqi || 'N/A'}
            </div>
            <div className="flex-1">
              <div className="text-white text-2xl font-bold mb-2">{getAQILevel(data.aqi || 0)}</div>
              <p className="text-white/70">{getAQIDescription(data.aqi || 0)}</p>
              <div className="mt-4 text-white/60 text-sm">
                <p>📍 Location: {selectedCity}</p>
                <p>🕐 Updated: {new Date().toLocaleTimeString()}</p>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-gray-800 rounded-2xl p-6 mb-6 shadow-xl">
          <h3 className="text-white text-xl font-bold mb-4">AQI Scale Reference</h3>
          <div className="space-y-3">
            <div className="flex items-center gap-4">
              <div className="bg-green-500 w-16 h-16 rounded-lg flex items-center justify-center text-white font-bold text-sm">0-50</div>
              <div className="text-white">
                <div className="font-bold">Good</div>
                <div className="text-sm text-white/60">Air quality is satisfactory and poses little or no risk</div>
              </div>
            </div>
            <div className="flex items-center gap-4">
              <div className="bg-yellow-500 w-16 h-16 rounded-lg flex items-center justify-center text-white font-bold text-sm">51-100</div>
              <div className="text-white">
                <div className="font-bold">Moderate</div>
                <div className="text-sm text-white/60">Air quality is acceptable for most people</div>
              </div>
            </div>
            <div className="flex items-center gap-4">
              <div className="bg-orange-500 w-16 h-16 rounded-lg flex items-center justify-center text-white font-bold text-sm">101-150</div>
              <div className="text-white">
                <div className="font-bold">Unhealthy for Sensitive Groups</div>
                <div className="text-sm text-white/60">Sensitive groups may experience health effects</div>
              </div>
            </div>
            <div className="flex items-center gap-4">
              <div className="bg-red-500 w-16 h-16 rounded-lg flex items-center justify-center text-white font-bold text-sm">151-200</div>
              <div className="text-white">
                <div className="font-bold">Unhealthy</div>
                <div className="text-sm text-white/60">Everyone may begin to experience health effects</div>
              </div>
            </div>
            <div className="flex items-center gap-4">
              <div className="bg-purple-500 w-16 h-16 rounded-lg flex items-center justify-center text-white font-bold text-sm">201-300</div>
              <div className="text-white">
                <div className="font-bold">Very Unhealthy</div>
                <div className="text-sm text-white/60">Health alert: everyone may experience serious effects</div>
              </div>
            </div>
            <div className="flex items-center gap-4">
              <div className="bg-red-900 w-16 h-16 rounded-lg flex items-center justify-center text-white font-bold text-sm">300+</div>
              <div className="text-white">
                <div className="font-bold">Hazardous</div>
                <div className="text-sm text-white/60">Health warning: emergency conditions</div>
              </div>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {cities.map(city => {
            const cityAQI = citiesAQI[city];
            
            return (
              <div key={city} className="bg-gray-800 rounded-2xl p-6 shadow-xl hover:shadow-2xl transition-shadow">
                <h3 className="text-white text-lg font-bold mb-3">{city}</h3>
                <div className="flex items-center justify-between">
                  <div className={`text-3xl font-bold text-white px-4 py-2 rounded-lg ${getAQIColor(cityAQI || 0)}`}>
                    {cityAQI || 'N/A'}
                  </div>
                  <div className="text-white text-right">
                    <div className="font-bold">{getAQILevel(cityAQI || 0)}</div>
                    <div className="text-sm text-white/60">Real-time</div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>

        <div className="mt-6 p-4 bg-blue-900/30 rounded-lg border border-blue-500/50">
          <p className="text-white/80 text-sm">
            <strong>ℹ️ Note:</strong> AQI data is provided by OpenWeatherMap Air Pollution API. 
            The free tier may have limited coverage for some locations. AQI values are converted 
            from the European Air Quality Index scale to the US EPA AQI scale for consistency.
          </p>
        </div>
      </div>
    </div>
  );
};

export default AQIPage;