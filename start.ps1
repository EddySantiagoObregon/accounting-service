# Script de inicio rápido para Microservicios de Banca
# Autor: Prueba Técnica
# Descripción: Inicia todos los servicios necesarios

Write-Host "🚀 Iniciando Microservicios de Banca..." -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

# Verificar si Docker está ejecutándose
try {
    docker info | Out-Null
    Write-Host "✅ Docker está ejecutándose" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: Docker no está ejecutándose. Por favor, inicia Docker Desktop." -ForegroundColor Red
    exit 1
}

# Verificar si Docker Compose está disponible
try {
    docker-compose --version | Out-Null
    Write-Host "✅ Docker Compose está disponible" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: Docker Compose no está instalado." -ForegroundColor Red
    exit 1
}

# Detener contenedores existentes
Write-Host "🛑 Deteniendo contenedores existentes..." -ForegroundColor Yellow
docker-compose down

# Construir y levantar los servicios
Write-Host "🔨 Construyendo y levantando servicios..." -ForegroundColor Yellow
docker-compose up --build -d

# Esperar a que los servicios estén listos
Write-Host "⏳ Esperando a que los servicios estén listos..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Verificar el estado de los servicios
Write-Host "🔍 Verificando estado de los servicios..." -ForegroundColor Yellow
docker-compose ps

# Verificar health checks
Write-Host "🏥 Verificando health checks..." -ForegroundColor Yellow

# Customer Service
Write-Host "Verificando Customer Service..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -TimeoutSec 5
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Customer Service está funcionando (Puerto 8081)" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Customer Service no está respondiendo" -ForegroundColor Red
}

# Accounting Service
Write-Host "Verificando Accounting Service..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8082/actuator/health" -TimeoutSec 5
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Accounting Service está funcionando (Puerto 8082)" -ForegroundColor Green
    }
} catch {
    Write-Host "❌ Accounting Service no está respondiendo" -ForegroundColor Red
}

Write-Host ""
Write-Host "🎉 ¡Microservicios iniciados exitosamente!" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Servicios disponibles:" -ForegroundColor Cyan
Write-Host "   • Customer Service: http://localhost:8081" -ForegroundColor White
Write-Host "   • Accounting Service: http://localhost:8082" -ForegroundColor White
Write-Host "   • MySQL: localhost:3306" -ForegroundColor White
Write-Host "   • Kafka: localhost:9092" -ForegroundColor White
Write-Host ""
Write-Host "📚 Documentación:" -ForegroundColor Cyan
Write-Host "   • README.md - Instrucciones completas" -ForegroundColor White
Write-Host "   • Microservicios_Banca.postman_collection.json - Colección de Postman" -ForegroundColor White
Write-Host "   • BaseDatos.sql - Script de base de datos" -ForegroundColor White
Write-Host ""
Write-Host "🔧 Comandos útiles:" -ForegroundColor Cyan
Write-Host "   • Ver logs: docker-compose logs -f" -ForegroundColor White
Write-Host "   • Detener servicios: docker-compose down" -ForegroundColor White
Write-Host "   • Reiniciar: docker-compose restart" -ForegroundColor White
Write-Host ""
Write-Host "✨ ¡Listo para usar!" -ForegroundColor Green
