#!/bin/bash

# Script de inicio rápido para Microservicios de Banca
# Autor: Prueba Técnica
# Descripción: Inicia todos los servicios necesarios

echo "🚀 Iniciando Microservicios de Banca..."
echo "========================================"

# Verificar si Docker está ejecutándose
if ! docker info > /dev/null 2>&1; then
    echo "❌ Error: Docker no está ejecutándose. Por favor, inicia Docker Desktop."
    exit 1
fi

# Verificar si Docker Compose está disponible
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Error: Docker Compose no está instalado."
    exit 1
fi

echo "✅ Docker está ejecutándose"
echo "✅ Docker Compose está disponible"

# Detener contenedores existentes
echo "🛑 Deteniendo contenedores existentes..."
docker-compose down

# Construir y levantar los servicios
echo "🔨 Construyendo y levantando servicios..."
docker-compose up --build -d

# Esperar a que los servicios estén listos
echo "⏳ Esperando a que los servicios estén listos..."
sleep 30

# Verificar el estado de los servicios
echo "🔍 Verificando estado de los servicios..."
docker-compose ps

# Verificar health checks
echo "🏥 Verificando health checks..."

# Customer Service
echo "Verificando Customer Service..."
if curl -s http://localhost:8081/actuator/health > /dev/null; then
    echo "✅ Customer Service está funcionando (Puerto 8081)"
else
    echo "❌ Customer Service no está respondiendo"
fi

# Accounting Service
echo "Verificando Accounting Service..."
if curl -s http://localhost:8082/actuator/health > /dev/null; then
    echo "✅ Accounting Service está funcionando (Puerto 8082)"
else
    echo "❌ Accounting Service no está respondiendo"
fi

echo ""
echo "🎉 ¡Microservicios iniciados exitosamente!"
echo ""
echo "📋 Servicios disponibles:"
echo "   • Customer Service: http://localhost:8081"
echo "   • Accounting Service: http://localhost:8082"
echo "   • MySQL: localhost:3306"
echo "   • Kafka: localhost:9092"
echo ""
echo "📚 Documentación:"
echo "   • README.md - Instrucciones completas"
echo "   • Microservicios_Banca.postman_collection.json - Colección de Postman"
echo "   • BaseDatos.sql - Script de base de datos"
echo ""
echo "🔧 Comandos útiles:"
echo "   • Ver logs: docker-compose logs -f"
echo "   • Detener servicios: docker-compose down"
echo "   • Reiniciar: docker-compose restart"
echo ""
echo "✨ ¡Listo para usar!"
