## Run in terminal if tests failed
- $env:DATABASE_URL="jdbc:postgresql://localhost:5432/dashboard"
- $env:DATABASE_USERNAME="postgres"
- $env:DATABASE_PASSWORD="dashboardapp"
- ./mvnw package

## Docker deployment
- docker build -t demo-deployment .
- docker tag demo-deployment alexnehoi/demo-deployment:latest
- docker push alexnehoi/demo-deployment:latest