@echo off
echo Stopping existing containers...
docker-compose down
echo.
echo Starting containers for DEVELOPMENT...
docker-compose up --build -d
echo.
echo Development environment started.
pause
