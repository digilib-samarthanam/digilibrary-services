# digital library services
Micro services project springboot

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
    "mobileNumber": "999999998",
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

