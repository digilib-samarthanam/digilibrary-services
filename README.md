# Digital Library Services
Micro services project springboot

### Swagger UI URL  
http://host/swagger-ui/ e.g.- http://localhost:8080/swagger-ui/

# Build
### Gradle
To build the application(MAC):

```
$ ./gradlew clean build
```

# Run
To run the application ... Assuming that you already downloaded the application and are in the project folder.

```
$ ./gradlew bootRun
```

#Application starts at

```
http://localhost:8080/books/v1/pdfs/1  - Read PDF in browser
http://localhost:8080/books/v1/audios/1  - Download audio file(mp3)
```

# Endpoints

### signup 
```
curl --location --request POST 'http://localhost:8080/user/signup' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=2A8722AF0263477D4EC8AE5C8024F173' \
--data-raw '{
    "firstName": "jon",
    "lastName": "doe",
    "emailAddress": "jon@gmail.com",
    "password": "hello",
    "gender": "F"
}'
```

### signup verify
```
curl --location --request GET 'http://localhost:8080/user/signup/verify?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXlsb2FkIjoie1widXNlclNlcXVlbmNlSWRcIjo0fSIsImlzcyI6IlNhbWFydGhhbmFtLURpZ2ktTGlicmFyeSIsImV4cCI6MTYwMDY2MTgxOSwianRpIjoiNmQwYWZjNWMtZDZjYy00NThjLTg3NTEtN2FlYmI5ZGRmYzkwIn0.mf6ftZ4a4fDL1_SBfvXHahlRp8jbPD2oi-iYE70LS1U' \
--header 'Cookie: JSESSIONID=2A8722AF0263477D4EC8AE5C8024F173'
```

### login
```
curl --location --request POST 'http://localhost:8080/user/login' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=2A8722AF0263477D4EC8AE5C8024F173' \
--data-raw '{
    "email": "jon@gmail.com",
    "password": "hello"
}'
```

### forgot password

```
curl --location --request POST 'http://localhost:8080/user/password/forgot' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=2A8722AF0263477D4EC8AE5C8024F173' \
--data-raw '{
    "email": "ruta@gmail.com"
}'
```

### update password

```
curl --location --request POST 'http://localhost:8080/user/password/update' \
--header 'token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXlsb2FkIjoie1wiZW1haWxcIjpcInJ1dGFAZ21haWwuY29tXCJ9IiwiaXNzIjoiU2FtYXJ0aGFuYW0tRGlnaS1MaWJyYXJ5IiwiZXhwIjoxNjAxNzI0MzA1LCJqdGkiOiJiN2E0NTkxNy02NTA2LTQxM2EtOGZkZi03YWNlOWIzODZiM2EifQ.nL044gb7QWnxzi6kTP92vPd4ezvmShyGK7W73b01ZIk' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=2A8722AF0263477D4EC8AE5C8024F173' \
--data-raw '{
    "password": "mynameisruta"
}'
```

