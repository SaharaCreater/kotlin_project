#!/bin/bash
# DoPP Physics AR — Server Setup Script
# Run on your Beget VPS (85.198.67.191) as root or sudo user:
#   chmod +x setup.sh && sudo ./setup.sh

set -e

echo "=== DoPP Physics AR — Server Setup ==="

# Install Node.js 20 LTS if not present
if ! command -v node &> /dev/null; then
    echo "[1/5] Installing Node.js 20..."
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
    apt-get install -y nodejs
else
    echo "[1/5] Node.js already installed: $(node -v)"
fi

# Install PM2 for process management
if ! command -v pm2 &> /dev/null; then
    echo "[2/5] Installing PM2..."
    npm install -g pm2
else
    echo "[2/5] PM2 already installed"
fi

# Install dependencies
echo "[3/5] Installing dependencies..."
npm install

# Create data directory
mkdir -p data

# Generate JWT secret if .env doesn't exist
if [ ! -f .env ]; then
    echo "[4/5] Creating .env file..."
    JWT_SECRET=$(node -e "console.log(require('crypto').randomBytes(64).toString('hex'))")
    echo "JWT_SECRET=$JWT_SECRET" > .env
    echo "PORT=5000" >> .env
    echo ".env created with a random JWT secret."
else
    echo "[4/5] .env already exists — skipping"
fi

# Start / restart with PM2
echo "[5/5] Starting server with PM2..."
pm2 stop dopp-server 2>/dev/null || true
pm2 start server.js --name dopp-server --env production
pm2 save
pm2 startup 2>/dev/null || true

echo ""
echo "=== Done! Server is running on port 5000 ==="
echo "Check status:  pm2 status"
echo "View logs:     pm2 logs dopp-server"
echo "Restart:       pm2 restart dopp-server"
echo ""
echo "Open firewall port 5000 if needed:"
echo "  ufw allow 5000/tcp"
