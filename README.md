Spring Boot Kotlin Reactive Demo
--------------------------------

[![Run on Google Cloud](https://deploy.cloud.run/button.svg)](https://deploy.cloud.run)

Run Locally:
1. In another terminal: `./gradlew bootRun`
1. Open: [localhost:8080](http://localhost:8080)


Set GCP `PROJECT_ID`:
```
export PROJECT_ID=YOUR_GCP_PROJECT_ID
```

Create JVM Docker Image:
```
./gradlew bootBuildImage --imageName=gcr.io/$PROJECT_ID/springboot-kotlin-reactive-demo
```

Or create a GraalVM Native Image Docker Image:
```
./gradlew bootBuildImage --imageName=gcr.io/$PROJECT_ID/springboot-kotlin-reactive-demo -Pnative
```

Run Docker Image Locally:
```
docker run -it -p8080:8080 gcr.io/$PROJECT_ID/springboot-kotlin-reactive-demo
```

Run on Google Cloud Run:
```
docker push gcr.io/$PROJECT_ID/springboot-kotlin-reactive-demo

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
