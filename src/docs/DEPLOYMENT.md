## Run in terminal if tests failed
- $env:DATABASE_URL="jdbc:postgresql://localhost:5432/dashboard"
- $env:DATABASE_USERNAME="postgres"
- $env:DATABASE_PASSWORD="dashboardapp"
- ./mvnw package

## Docker 
#### This uploads your new image to Docker Hub, under your username (alexnehoi) and tag latest.
- run docker desktop
- docker build -t demo-deployment .
- docker tag demo-deployment alexnehoi/demo-deployment:latest
- docker push alexnehoi/demo-deployment:latest

## Deployment
#### Render pulls from Docker Hub the image tagged latest
- render.com (alexnehoi)
- select project
- manual deploy
- deploy latest reference
- db - neon (alexnehoi)