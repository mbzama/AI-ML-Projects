# PowerShell script to build and run Procure AI Application
param(
    [string]$Action = "run"
)

Write-Host "Procure AI - Build & Run Script" -ForegroundColor Cyan
Write-Host "===============================" -ForegroundColor Cyan

switch ($Action.ToLower()) {
    "clean" {
        Write-Host "Cleaning project..." -ForegroundColor Yellow
        mvn clean
    }
    "build" {
        Write-Host "Building project..." -ForegroundColor Yellow
        mvn clean compile
    }
    "test" {
        Write-Host "Running tests..." -ForegroundColor Yellow
        mvn test
    }
    "package" {
        Write-Host "Packaging application..." -ForegroundColor Yellow
        mvn clean package -DskipTests
    }
    "run" {
        Write-Host "Building and running application..." -ForegroundColor Yellow
        mvn clean package -DskipTests
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Starting application on http://localhost:8080" -ForegroundColor Green
            java -jar target/procure-api-0.0.1-SNAPSHOT.jar
        } else {
            Write-Host "Build failed!" -ForegroundColor Red
        }
    }
    "dev" {
        Write-Host "Running in development mode..." -ForegroundColor Yellow
        mvn spring-boot:run -Dspring-boot.run.profiles=dev
    }
    default {
        Write-Host "Usage: .\build-and-run.ps1 [clean|build|test|package|run|dev]" -ForegroundColor White
        Write-Host "Default action is 'run'" -ForegroundColor White
    }
}
