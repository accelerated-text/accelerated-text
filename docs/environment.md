# Environment setup

## Prerequisites

* [Make](https://www.gnu.org/software/make/)
* [Docker-compose](https://docs.docker.com/compose/install/)

## Configuration

Refer to *docker-compose.yml* at the project root.

To change languages displayed in Accelerated Text UI, change the `ENABLED_LANGUAGES` variable.

Supported languages are:

* English
* Estonian
* German
* Latvian
* Russian
* Spanish

## Launch

Navigate to project root and type:

```
make run-dev-env
```

After build is complete and environment is running, you should see:

```
INFO  a.server - Running server on: localhost:3001. Press Ctrl+C to stop
```
