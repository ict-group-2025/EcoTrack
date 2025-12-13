import React, { useState, useEffect } from 'react';
import { Cloud } from 'lucide-react';

const Header = () => {
  const [currentTime, setCurrentTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setCurrentTime(new Date()), 60000);
    return () => clearInterval(timer);
  }, []);

  return (
    <header className="bg-gray-800 p-4 shadow-lg">
      <div className="max-w-7xl mx-auto flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="bg-gradient-to-br from-blue-400 to-yellow-400 p-2 rounded-lg">
            <Cloud className="w-6 h-6 text-white" />
          </div>
          <h1 className="text-2xl font-bold">Environmental Monitoring</h1>
        </div>
        <div className="text-xl font-semibold">
          {currentTime.toLocaleTimeString('en-US', { 
            hour: '2-digit', 
            minute: '2-digit', 
            hour12: true 
          })}
        </div>
      </div>
    </header>
  );
};

export default Header;