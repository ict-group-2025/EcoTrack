export const getAQIColor = (aqi) => {
  if (aqi <= 50) return 'bg-green-500';
  if (aqi <= 100) return 'bg-yellow-500';
  if (aqi <= 150) return 'bg-orange-500';
  return 'bg-red-500';
};

export const getAQILevel = (aqi) => {
  if (aqi <= 50) return 'Good';
  if (aqi <= 100) return 'Moderate';
  if (aqi <= 150) return 'Unhealthy for Sensitive Groups';
  if (aqi <= 200) return 'Unhealthy';
  if (aqi <= 300) return 'Very Unhealthy';
  return 'Hazardous';
};

export const getAQIDescription = (aqi) => {
  if (aqi <= 50) return 'Air quality is satisfactory, and air pollution poses little or no risk.';
  if (aqi <= 100) return 'Air quality is acceptable. However, there may be a risk for some people.';
  if (aqi <= 150) return 'Members of sensitive groups may experience health effects.';
  return 'Everyone may begin to experience health effects.';
};

export const convertTemp = (temp, unit) => {
  if (unit === 'F') {
    return Math.round((temp * 9/5) + 32);
  }
  return temp;
};