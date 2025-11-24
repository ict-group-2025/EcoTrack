import React from 'react';
import { Sun, CloudRain, Cloud } from 'lucide-react';

const WeatherCard = ({ hour, tempUnit, convertTemp }) => {
  const getWeatherIcon = (icon) => {
    switch(icon) {
      case 'sun': return <Sun className="w-8 h-8" />;
      case 'rain': return <CloudRain className="w-8 h-8" />;
      case 'cloud': return <Cloud className="w-8 h-8" />;
      case 'storm': return <CloudRain className="w-8 h-8" />;
      default: return <Cloud className="w-8 h-8" />;
    }
  };

  return (
    <div className="flex-shrink-0 bg-gray-700 rounded-2xl p-4 text-center min-w-[100px]">
      <div className="text-white/60 text-sm mb-2">{hour.time}</div>
      <div className="text-white mb-2">{getWeatherIcon(hour.icon)}</div>
      <div className="text-white text-xl font-bold">
        {convertTemp(hour.temp, tempUnit)}°
      </div>
    </div>
  );
};

export default WeatherCard;