const config = {
  development: {
    API_URL: 'http://localhost:8080/api',
    ENABLE_LOGS: true
  },
  production: {
    API_URL: '/api',
    ENABLE_LOGS: false
  }
};

const env = import.meta.env.MODE || 'development';

export default config[env] || config.development;
