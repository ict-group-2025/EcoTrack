import React from 'react';

const Navigation = ({ activeTab, setActiveTab }) => {
  const tabs = ['Home', 'Live Data', 'Prediction', 'History/Trends', 'About'];

  return (
    <nav className="bg-gray-800 border-b border-gray-700">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex gap-8">
          {tabs.map(tab => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab.toLowerCase().replace('/', ''))}
              className={`py-4 px-2 border-b-2 transition-colors ${
                activeTab === tab.toLowerCase().replace('/', '')
                  ? 'border-purple-500 text-white'
                  : 'border-transparent text-white/60 hover:text-white'
              }`}
            >
              {tab}
            </button>
          ))}
        </div>
      </div>
    </nav>
  );
};

export default Navigation;