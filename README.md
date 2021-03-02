# cashew
Generic multi-payment instrument acceptance service

To run the service do the following:

1. Install Postgres or spin up a postgres docker
2. If you do not want to install Postgres, uncomment the H2 database configuration in the config/application.properties file.
3. Build the application:
```java
   gradle clean build
```
4. Run the application:
```java
    java -jar chashewpay.jar
```
5. Access it from http://localhost:5615.
```javascript
  {"app":"Cashew Payments","status":"Running. Healthy.","uptime":"202secs"}
```

The following endpoints are available:
1. http://127.0.0.1:5615 Readiness
2. http://127.0.0.1:5615/ping Health check
3. http://127.0.0.1:5615/auth POST form data for authentication
4. http://127.0.0.1:5615/payments GET/POST Send new payment or list all payments
5. https://127.0.0.1:5615/payments/card GET List all card payments
6. http://127.0.0.1:5615/payments/bank GET List all bank payments
7. https://127.0.0.1:5615/payments/mobile GET List all mobile payments


Obtain a token by sending a post form request to http://127.0.0.1:5615/auth with credentials username and password having values `admin` and `admin` respectively.

```javascript
{
    "error": "00",
    "description": "Success",
    "data": "eyJhbGciOiJIUzUxMiJ9.eyJqdGkiOiJDYXNoZXctUGF5bWVudHMiLCJzdWIiOiJhZG1pbiIsImF1dGhvcml0aWVzIjpbIlJPTEVfVVNFUiJdLCJpYXQiOjE2MTQ2Nzg4MzQsImV4cCI6MTYxNDY3OTQzNH0.NuPEQYfY46HF8bNb1dqZ65nvQNkr9Wqkpjp831bfFm-HPEPSBUM0e_1HTXjHxXxIeFJ2bE64ahP7lN5WZO1vFg",
    "redirect": false,
    "failed": false,
    "success": true
}
```

Use the token in the request header:
GET http://127.0.0.1:5615/payments
Authentication: Bearer + token

```javascript
[
    {
        "id": "66252400010000000001",
        "reference": "PAY-REF-000375",
        "email": "test.email@unit.com",
        "merchant": "Amala-Store",
        "amount": 8901,
        "status": "12",
        "date": 1614630215265,
        "mobile": "234901234567"
    },
    {
        "id": "36450200280000000008",
        "reference": "MBD-090-230000",
        "email": "Segun@gmail.com",
        "merchant": "Benson",
        "amount": 78950,
        "status": "12",
        "date": 1614630765463,
        "account": "4500000012",
        "bank": "092"
    }
]
```
POST http://127.0.0.1:5615/payments
Authentication: Bearer + token
Sample request bank payment
```javascript
{
    "merchant": "Benson",
    "amount": "78950",
    "method": "BANK",
    "account": "4500000012",
    "bank": "092",
    "email": "Segun@gmail.com",
    "reference":"MBD-090-230000",
    "hash":"000040"
}
```
Sample response for success
```
{
    "error": "00",
    "description": "Success",
    "data": {
        "reference": "MBD-090-230000",
        "id": "11344300280000000002",
        "status": "00"
    },
    "redirect": false,
    "failed": false,
    "success": true
}
```

Sample response for mismatched hash
```javascript
{
    "error": "59",
    "description": "Bad request or attempted fraud.",
    "data": {
        "reference": "MBD-090-230000",
        "id": "75679400280000000001",
        "status": "59"
    },
    "redirect": false,
    "failed": true,
    "success": false
}
```


