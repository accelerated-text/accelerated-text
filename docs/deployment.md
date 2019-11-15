# Deployment

## Quick start

The most straight forward way is to run
`make run-dev-env`

In case only API is required:

`make run-dev-api`

## Requirements

If running everything on `Docker` stack, only `docker` and `docker-compose` are required. If you prefer to run directly, each part is described bellow.

## Building blocks

This project consists of serveral independant parts which are combined together using `docker-compose`, which relies on [Docker](https://www.docker.com/) containers. Each individual parts has it's `Dockerfile` which can be used as a reference launching directly on machine.

### Localstack

(api/Dockerfile.localstack)

Database part. Either [DynamoDB](https://aws.amazon.com/dynamodb/) (which can be simply launched on AWS), or [Datomic](https://www.datomic.com/) can be used. 

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

### GF

(core/gf/Dockerfile)

It launches an instance of [Grammatical Framework](https://www.grammaticalframework.org) with a custom REST API to be able to generate text and get result by single HTTP request.

Grammatical Framework can be downloaded and installed directly ([Downloads Page](https://www.grammaticalframework.org/download/index.html)) and `core/gf/server.py` can be simply launched via `python server.py` or `gunicorn server`(Works on Python 2.7 and Python 3+). 

If `pgf` module is missing, that means runtime libraries are not installed. Runtime Libraries can be found [here](https://github.com/GrammaticalFramework/gf-core/tree/master/src/runtime), you always need to compile `C` runtime library and only then compile `python` (same goes for other runtimes, C is required)

### Front-End

(front-end/Dockerfile)

FrontEnd runs on nodeJS, thus `node` and `npm` should be installed to be able to run it directly.

It can be launched via `make run-front-end-dev`, or if you prefer `npm`, `npm install && npm run` inside `front-end` directory
