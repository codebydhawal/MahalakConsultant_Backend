# Deploying MahalakMedia to Render

## Before creating the service

1. Push this repository to GitHub. Do not commit Google credential JSON files or any `.env` file.
2. In Google Cloud Console, create or use an OAuth 2.0 client of type **Web application**. Add this exact authorized redirect URI to the client used by `google-drive-oauth.json`:
   `https://<your-render-service-name>.onrender.com/oauth2/callback`
3. Share the target Google Sheet with the service-account email found in the Google Sheets credential JSON, granting it **Editor** access.
4. Share the target Google Drive folder with the Google account that will complete the Drive OAuth login.

## Create the Render service

Create a **Web Service** from the GitHub repository with these commands:

```
Build Command: ./mvnw clean package -DskipTests
Start Command: java -jar target/MahalakMedia-0.0.1-SNAPSHOT.jar
```

Use Java 17. Render provides `PORT`; the application now listens on it automatically.

## Add secret files

In the Render service's **Environment** page, upload these as secret files:

| Local credential | Secret-file path on Render |
| --- | --- |
| Google OAuth client credential JSON for Drive | `/etc/secrets/google-drive-oauth.json` |
| Google service-account JSON for Sheets | `/etc/secrets/google-sheets-service-account.json` |

Do not paste either credential into GitHub or the source code.

## Set environment variables

Add the following in Render's **Environment** page. Use the exact values from your PostgreSQL provider and Google configuration.

```
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/<database>?sslmode=require
SPRING_DATASOURCE_USERNAME=<database-user>
SPRING_DATASOURCE_PASSWORD=<database-password>
SPRING_JPA_HIBERNATE_DDL_AUTO=update

APP_JWT_SECRET=<a-new-random-secret-of-at-least-32-characters>
APP_CORS_ALLOWED_ORIGINS=https://<your-frontend-domain>

GOOGLE_DRIVE_FOLDER_ID=<your-existing-folder-id>
GOOGLE_SHEET_SPREADSHEET_ID=<your-existing-spreadsheet-id>
GOOGLE_DRIVE_CREDENTIALS_PATH=/etc/secrets/google-drive-oauth.json
GOOGLE_SHEET_CREDENTIALS_PATH=/etc/secrets/google-sheets-service-account.json
GOOGLE_OAUTH_REDIRECT_URI=https://<your-render-service-name>.onrender.com/oauth2/callback

APP_BOOTSTRAP_ROLES_ENABLED=true
```

`SPRING_JPA_HIBERNATE_DDL_AUTO=update` creates/updates the schema. On a new database the application also creates `ADMIN`, `STAFF`, and `CUSTOMER` roles if they do not already exist.

## Persist the Google Drive login

The first time the deployed service uses Google Drive, open:

```
https://<your-render-service-name>.onrender.com/google/login
```

Complete the Google login. The resulting refresh token is saved in the configured PostgreSQL database's `google_oauth_tokens` table.

Because the refresh token is in PostgreSQL, Render restarts and redeploys do not require a persistent disk or another Google login. Repeat the login only if the Google refresh token is revoked or becomes invalid.

## Verify after deployment

1. Check the Render logs for a successful Spring Boot startup.
2. Open `https://<your-render-service-name>.onrender.com/swagger-ui/index.html`.
3. Confirm the `Role` table contains `ADMIN`, `STAFF`, and `CUSTOMER`.
4. Test one Google Sheet action and one Drive upload.
