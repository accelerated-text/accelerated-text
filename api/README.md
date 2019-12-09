# About

This module provides API access to the text generation functionality defined in Accelerated Text `core` module. 
It also ensures connectivity to all the data bases which provide data for the text generation. This way `core`
is completely separated from the data storage mechanics.

## Running locally

Start one of the database servers we support [*Datomic*](https://docs.datomic.com/on-prem/get-datomic.html) or 
[*DynamoDB*](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html). 
Once it is running start API server with the following command

- `make run-local-server`

After execution API will be reachable via `http://localhost:8080/`

# graphql-api

NLG API backend as a GraphQL.

GraphQL implementation is based on (Lacinia)[https://github.com/walmartlabs/lacinia].

Schema definition is in the `resources/schema.edn` file.

GraphQL implementation is in `src/api/graphql/core.clj`.

Deployment targets include:
- AWS lambda under the API Gateway (DONE)
- HTTP server (TODO)

Calling backend via API Gateway:

```clojure
(time @(http/request
         {:method :post
          :url    "https://3mf0phba57.execute-api.eu-central-1.amazonaws.com/Prod/_graphql"
          :body   (json/write-value-as-string {"my" "graphql-request"})
          :client @client}))
"Elapsed time: 70.667672 msecs"
=>
{:opts {:method :post,
        :url "https://3mf0phba57.execute-api.eu-central-1.amazonaws.com/Prod/_graphql",
        :body "{\"my\":\"graphql-request\"}",
        :client #object[org.httpkit.client.HttpClient 0x48da91aa "org.httpkit.client.HttpClient"]},
 :body "{\"data\":{\"hero\":{\"id\":2000,\"name\":\"Lando Calrissian\"}}}",
 :headers {:date "Tue, 21 May 2019 08:07:11 GMT",
           :x-amzn-trace-id "Root=1-5ce3b1af-602c8c56604292a8a59b9dac;Sampled=1",
           :via "1.1 6f44cdfb15fbc531a6e5744b23d2e9e2.cloudfront.net (CloudFront)",
           :x-cache "Miss from cloudfront",
           :content-length "55",
           :x-amzn-requestid "6fe536a6-7b9f-11e9-b943-1d4c42b6ca02",
           :content-type "application/json",
           :x-amz-cf-id "8Yb5rU4xF9rvx2JYB4XX8xx1bqjEWVDgWcc78vxsNvqhMC-RMjwFHg==",
           :x-amz-apigw-id "aBizeGM1liAFUMg=",
           :connection "keep-alive"},
 :status 200}
```
