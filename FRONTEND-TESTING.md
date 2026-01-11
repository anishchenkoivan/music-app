# Testing the Frontend Locally

There are two ways to test the frontend locally:

## Option 1: Using Docker (Recommended - Full Stack)

This will run the entire application including all backend services and the frontend.

### Steps:

1. **Ensure you have the `.env` file** (already created):
```bash
cat .env
```

2. **Build and start all services**:
```bash
docker compose -f docker-compose.deploy.yml up --build -d
```

3. **Wait for services to start** (this may take a few minutes on first run):
```bash
# Check service status
docker compose -f docker-compose.deploy.yml ps

# Watch logs
docker compose -f docker-compose.deploy.yml logs -f frontend
```

4. **Access the application**:
   - Frontend: http://localhost:3000
   - Backend API Gateway: http://localhost:8080
   - Eureka Dashboard: http://localhost:8761

5. **Stop all services when done**:
```bash
docker compose -f docker-compose.deploy.yml down
```

### Troubleshooting Docker Setup:

If the frontend container fails to build, check logs:
```bash
docker compose -f docker-compose.deploy.yml logs frontend
```

If you need to rebuild just the frontend:
```bash
docker compose -f docker-compose.deploy.yml up --build -d frontend
```

---

## Option 2: Development Mode (Frontend Only)

This runs only the frontend in development mode. You'll need the backend services running separately.

### Prerequisites:
- Node.js 18+ installed
- Backend services running (via docker-compose or separately)

### Steps:

1. **Navigate to the web directory**:
```bash
cd web
```

2. **Install dependencies**:
```bash
npm install
```

3. **Start the development server**:
```bash
npm start
```

4. **Access the application**:
   - Frontend: http://localhost:3000
   - It will automatically connect to the backend at http://localhost:8080

### Development Mode Features:
- Hot reload on file changes
- Better error messages
- React DevTools support
- Faster iteration

### To stop development server:
Press `Ctrl+C` in the terminal

---

## Testing the Application

### 1. Register a New User
- Go to http://localhost:3000
- Click "Register"
- Fill in username, email, and password
- Submit the form

### 2. Login
- After registration, you'll be redirected to login
- Enter your credentials
- You'll be redirected to the tracks page

### 3. Browse Content
- **Tracks**: View all available tracks
- **Albums**: Browse albums
- **Playlists**: View and manage playlists
- **Favorites**: Manage your favorite tracks

### 4. Test API Connection
Open browser console (F12) and check for:
- Network requests to http://localhost:8080/api/*
- Any error messages
- JWT token in localStorage

---

## Configuration for Different Environments

### For Docker Deployment:

Edit the `.env` file:
```bash
# For localhost
API_URL=http://localhost:8080

# For production domain
API_URL=https://api.yourdomain.com

# For server IP
API_URL=http://192.168.1.100:8080
```

Then restart the frontend service:
```bash
docker compose -f docker-compose.deploy.yml up -d frontend
```

### For Development Mode:

The API URL is configured in `web/src/config.js` and defaults to `http://localhost:8080`.

---

## Common Issues

### Issue: Frontend shows "Failed to load"
**Solution**: Ensure backend services are running:
```bash
docker compose -f docker-compose.deploy.yml ps
```

### Issue: CORS errors in browser console
**Solution**: The gateway service should handle CORS. Check gateway logs:
```bash
docker compose -f docker-compose.deploy.yml logs gateway_service
```

### Issue: "Cannot connect to backend"
**Solution**: Verify the API_URL is correct and the gateway is accessible:
```bash
curl http://localhost:8080/actuator/health
```

### Issue: Port 3000 already in use
**Solution**: Stop other services using port 3000 or change the port in docker-compose.deploy.yml:
```yaml
frontend:
  ports:
    - "3001:80"  # Change 3000 to 3001
```

---

## Quick Start Commands

### Full stack with Docker:
```bash
# Start everything
docker compose -f docker-compose.deploy.yml up -d

# View logs
docker compose -f docker-compose.deploy.yml logs -f

# Stop everything
docker compose -f docker-compose.deploy.yml down
```

### Development mode:
```bash
# Terminal 1: Start backend
docker compose -f docker-compose.deploy.yml up -d

# Terminal 2: Start frontend dev server
cd web && npm install && npm start
```
