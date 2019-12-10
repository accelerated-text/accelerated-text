# Deployment

## Quick start

The most straight forward way is to run
`make run-dev-env`

In case only API is required:

`make run-dev-api`

## Requirements

If running everything on `Docker` stack, only `docker` and `docker-compose` are required. If you prefer to run directly, each part is described bellow.

## Building blocks

This project consists of serveral independant parts which are combined together using `docker-compose`, which relies on [Docker](https://www.docker.com/) containers. Each individual part has it's `Dockerfile` which can be used as a reference launching directly on machine.

### Localstack

(api/Dockerfile.localstack)

Database part. Either [DynamoDB](https://aws.amazon.com/dynamodb/) (which can be simply launched on AWS), or [Datomic](https://www.datomic.com/) can be used.

Control which database to use via `DB_IMPLEMENTATION` environment variable:
* datomic - for Datomic
* dynamodb - for DynamoDB

### GF

(core/gf/Dockerfile)

It launches an instance of [Grammatical Framework](https://www.grammaticalframework.org) with a custom REST API to be able to generate text and get result by single HTTP request.

Grammatical Framework can be downloaded and installed directly ([Downloads Page](https://www.grammaticalframework.org/download/index.html)) and `core/gf/server.py` can be simply launched via `python server.py` or `gunicorn server`(Works on Python 2.7 and Python 3+). 

If `pgf` module is missing, that means runtime libraries are not installed. Runtime Libraries can be found [here](https://github.com/GrammaticalFramework/gf-core/tree/master/src/runtime), you always need to compile `C` runtime library and only then compile `python` (same goes for other runtimes, C is required)


### API

(api/Dockerfile)

It launches REST API as well as GraphQL. 

**GraphQL** gives access to:
- Document Plans
- Dictionary items
- Query uploaded CSV files

**REST API** gives access to:
- Request to generate text. Read generation result
- Upload CSV data file

It can be launched via: `make run-dev-api`, or `docker-compose -f docker-compose.yml up`

If you want to be able to run it locally, you firstly need to compile it. 
1. Install [Clojure](https://clojure.org/)
2. Do `clojure -A:uberjar`. It will create uberjar (fatjar) which contains all of the dependencies, code etc.
3. Copy `target/api-1.0.0-SNAPSHOT-standalone.jar` to server it will be running on

To run it, simply do `java -jar api-1.0.0-SNAPSHOT-standalone.jar` (Assuming server has Java JVM installed).
It does need to know what kind of database is used, where it is located and where GF can be found. 

All of these things are defined in environment variables:
```
DYNAMODB_ENDPOINT: http://localstack:4569
GF_ENDPOINT: http://gf:8000
DB_IMPLEMENTATION: dynamodb
```

### Front-End

(front-end/Dockerfile)

FrontEnd runs on nodeJS, thus `node` and `npm` should be installed to be able to run it directly.

It can be launched via `make run-front-end-dev`, or if you prefer `npm`, `npm install && npm run` inside `front-end` directory.

Because UI is essentially a "glue" which binds every part together, it needs to know where's all other parts are located.

It is done using environment variables.

Example.:
```
ACC_TEXT_API_URL: http://0.0.0.0:3001
ACC_TEXT_GRAPHQL_URL: http://0.0.0.0:3001/_graphql
MOCK_SHOP_API_URL: http://0:0:0:0:8090
```
