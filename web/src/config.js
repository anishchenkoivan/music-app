// Configuration for API endpoint
// This will be replaced at runtime by the nginx configuration
const config = {
  apiUrl: window.ENV?.API_URL || 'http://localhost:8080'
};

export default config;
