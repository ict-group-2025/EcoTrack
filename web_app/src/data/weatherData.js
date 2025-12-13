// Danh sách các thành phố Việt Nam
export const cities = [
  'Hanoi',
  'Ho Chi Minh City', 
  'Hai Phong',
  'Da Nang',
  'Can Tho',
  'Hue',
  'Nha Trang',
  'Vung Tau'
];

// Dữ liệu mặc định khi chưa load được API
export const defaultWeatherData = {
  temp: 0,
  humidity: 0,
  visibility: 0,
  aqi: 0,
  wind: 0,
  condition: 'Loading...',
  date: 'Loading...',
  hourly: []
};