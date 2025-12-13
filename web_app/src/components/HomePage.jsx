import React from 'react';
import { Cloud, Droplets, Eye, AlertCircle, Wind } from 'lucide-react';
import WeatherCard from './WeatherCard';
import { convertTemp } from '../utils/helpers';

const HomePage = ({ data, tempUnit }) => {
  return (
    <div className="flex-1 p-6">
      <div className="max-w-7xl mx-auto">
        <div className="bg-gradient-to-br from-purple-500 to-purple-900 rounded-3xl p-8 mb-6 shadow-2xl">
          <h2 className="text-white text-3xl font-bold mb-2 flex items-center gap-2">
            {data.city} <span className="text-2xl">📍</span>
          </h2>
          <p className="text-white/80 text-sm mb-8">{data.date}</p>
          
          <div className="flex items-center justify-center mb-8">
            <Cloud className="w-24 h-24 text-white/90 mr-4" />
            <div className="text-8xl font-bold text-white">
              {convertTemp(data.temp, tempUnit)}°{tempUnit}
            </div>
          </div>

          <div className="grid grid-cols-4 gap-4 text-white">
            <div className="text-center">
              <Droplets className="w-6 h-6 mx-auto mb-2" />
              <div className="text-xs opacity-80">HUMIDITY</div>
              <div className="text-xl font-bold">{data.humidity}%</div>
            </div>
            <div className="text-center">
              <Eye className="w-6 h-6 mx-auto mb-2" />
              <div className="text-xs opacity-80">VISIBILITY</div>
              <div className="text-xl font-bold">{data.visibility}km</div>
            </div>
            <div className="text-center">
              <AlertCircle className="w-6 h-6 mx-auto mb-2" />
              <div className="text-xs opacity-80">AQI</div>
              <div className="text-xl font-bold">{data.aqi}</div>
            </div>
            <div className="text-center">
              <Wind className="w-6 h-6 mx-auto mb-2" />
              <div className="text-xs opacity-80">WIND</div>
              <div className="text-xl font-bold">{data.wind}mph</div>
            </div>
          </div>
        </div>

        <div className="bg-gray-800 rounded-3xl p-6 shadow-xl">
          <h3 className="text-white text-xl font-bold mb-4">Hourly Forecast</h3>
          <div className="flex gap-4 overflow-x-auto pb-2">
            {data.hourly.map((hour, idx) => (
              <WeatherCard 
                key={idx} 
                hour={hour} 
                tempUnit={tempUnit}
                convertTemp={convertTemp}
              />
            ))}
          </div>
        </div>

        <div className="bg-teal-500 rounded-3xl p-6 mt-6 shadow-xl">
          <h3 className="text-white text-xl font-bold mb-2">Suggestion</h3>
          <p className="text-white text-lg">It is safe to go outside today</p>
          <button className="mt-4 bg-teal-600 hover:bg-teal-700 text-white px-4 py-2 rounded-lg text-sm">
            Ask AI Chatbot for more information
          </button>
        </div>
      </div>
    </div>
  );
};

export default HomePage;