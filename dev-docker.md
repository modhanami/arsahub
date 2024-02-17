> Disclaimer: This is a work in progress and intended for personal use.

**Build Backend image**

- ```shell
  cd backend
  ./gradlew jib
  ```
- Replace backend container's `JWT_SECRET` (`backend.environment.JWT_SECRET`) in `docker-compose.yml` with the secret
  from Supabase's JWT settings.

**Run all containers**

- ```docker-compose pull && docker-compose up -d --build```
- Open `localhost` or `localhost:80` in browser and login with Supabase credentials.



