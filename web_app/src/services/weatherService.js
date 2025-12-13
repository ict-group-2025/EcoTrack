const API_KEY = import.meta.env.VITE_OPENWEATHER_API_KEY;
const BASE_URL = import.meta.env.VITE_OPENWEATHER_BASE_URL;

// Lấy thông tin thời tiết hiện tại
export const getCurrentWeather = async (cityName) => {
  try {
    const response = await fetch(
      `${BASE_URL}/weather?q=${cityName}&appid=${API_KEY}&units=metric`
    );
    
    if (!response.ok) {
      throw new Error('Failed to fetch weather data');
    }
    
    const data = await response.json();
    return transformWeatherData(data);
  } catch (error) {
    console.error('Error fetching weather:', error);
    throw error;
  }
};

// Lấy dự báo 5 ngày (3 giờ một lần)
export const getForecast = async (cityName) => {
  try {
    const response = await fetch(
      `${BASE_URL}/forecast?q=${cityName}&appid=${API_KEY}&units=metric`
    );
    
    if (!response.ok) {
      throw new Error('Failed to fetch forecast data');
    }
    
    const data = await response.json();
    return transformForecastData(data);
  } catch (error) {
    console.error('Error fetching forecast:', error);
    throw error;
  }
};

// Lấy AQI (Air Quality Index)
export const getAirQuality = async (lat, lon) => {
  try {
    const response = await fetch(
      `${BASE_URL}/air_pollution?lat=${lat}&lon=${lon}&appid=${API_KEY}`
    );
    
    if (!response.ok) {
      throw new Error('Failed to fetch air quality data');
    }
    
    const data = await response.json();
    return transformAirQualityData(data);
  } catch (error) {
    console.error('Error fetching air quality:', error);
    return { aqi: 0 }; // Return default if AQI not available
  }
};

// Chuyển đổi dữ liệu OpenWeather sang format của app
const transformWeatherData = (data) => {
  return {
    temp: Math.round(data.main.temp),
    humidity: data.main.humidity,
    visibility: Math.round(data.visibility / 1000), // Convert to km
    wind: Math.round(data.wind.speed * 2.237), // Convert m/s to mph
    condition: data.weather[0].main,
    description: data.weather[0].description,
    icon: data.weather[0].icon,
    coords: {
      lat: data.coord.lat,
      lon: data.coord.lon
    },
    date: new Date().toLocaleDateString('en-US', { 
      month: 'short', 
      day: 'numeric',
      weekday: 'short'
    })
  };
};

// Chuyển đổi dữ liệu forecast
const transformForecastData = (data) => {
  // Lấy 8 dự báo đầu tiên (24 giờ tới)
  const hourly = data.list.slice(0, 8).map(item => ({
    time: new Date(item.dt * 1000).toLocaleTimeString('en-US', { 
      hour: 'numeric',
      hour12: true 
    }),
    temp: Math.round(item.main.temp),
    icon: getWeatherIconType(item.weather[0].icon)
  }));
  
  return { hourly };
};

// Chuyển đổi dữ liệu AQI
const transformAirQualityData = (data) => {
  if (!data.list || data.list.length === 0) {
    return { aqi: 0 };
  }
  
  // OpenWeather AQI: 1-5, chuyển sang 0-500
  const aqiMap = {
    1: 25,   // Good
    2: 75,   // Fair
    3: 125,  // Moderate
    4: 175,  // Poor
    5: 250   // Very Poor
  };
  
  const aqi = aqiMap[data.list[0].main.aqi] || 0;
  
  return {
    aqi,
    components: data.list[0].components
  };
};

// Map icon code từ OpenWeather sang icon type của app
const getWeatherIconType = (iconCode) => {
  const iconMap = {
    '01d': 'sun',
    '01n': 'sun',
    '02d': 'cloud',
    '02n': 'cloud',
    '03d': 'cloud',
    '03n': 'cloud',
    '04d': 'cloud',
    '04n': 'cloud',
    '09d': 'rain',
    '09n': 'rain',
    '10d': 'rain',
    '10n': 'rain',
    '11d': 'storm',
    '11n': 'storm',
    '13d': 'cloud',
    '13n': 'cloud',
    '50d': 'cloud',
    '50n': 'cloud'
  };
  
  return iconMap[iconCode] || 'cloud';
};

// Lấy tất cả dữ liệu cho một thành phố
export const getAllWeatherData = async (cityName) => {
  try {
    const currentWeather = await getCurrentWeather(cityName);
    const forecast = await getForecast(cityName);
    const airQuality = await getAirQuality(
      currentWeather.coords.lat, 
      currentWeather.coords.lon
    );
    
    return {
      ...currentWeather,
      ...forecast,
      aqi: airQuality.aqi
    };
  } catch (error) {
    console.error('Error fetching all weather data:', error);
    throw error;
  }
};