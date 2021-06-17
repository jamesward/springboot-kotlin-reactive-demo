Spring Boot Kotlin Reactive Demo
--------------------------------

[![Run on Google Cloud](https://deploy.cloud.run/button.svg)](https://deploy.cloud.run)

Run Locally (dev mode):
1. In a terminal: `./gradlew -t classes`
1. In another terminal: `./gradlew bootRun`
1. Open: [localhost:8080](http://localhost:8080)

Create Docker Image for GCP:
```
export PROJECT_ID=YOUR_GCP_PROJECT_ID
./gradlew bootBuildImage --imageName=gcr.io/$PROJECT_ID/springboot-kotlin-reactive-demo
docker push gcr.io/$PROJECT_ID/springboot-kotlin-reactive-demo
```

TODO: db setup stuff

Run Docker Image Locally:
```
docker run -p8080:8080 gcr.io/$PROJECT_ID/springboot-kotlin-reactive-demo
```

Run on Google Cloud Run:
```
gcloud run deploy \
  --image=gcr.io/$PROJECT_ID/springboot-kotlin-reactive-demo \
  --memory=1Gi \
  --cpu=2. \
  --platform=managed \
  --allow-unauthenticated \
  --project=$PROJECT_ID \
  --region=us-central1 \
  springboot-kotlin-reactive-demo
```

