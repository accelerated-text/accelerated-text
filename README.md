# Augmented Writer

Augmented Writter helps in building domain specific texts as well as constructing templates.

## Usage

### Dependencies

You should have NPM installed in your path. Get it from https://nodejs.org .

### Running

```bash
make run
```

### Running tests

```bash
make test
```

### CI test

Test are run in Gitlab CI. Test environment is a docker container that we prepare ourselves.

Publishing a Docker container with test env is a manual process and as of now must be done on developer PC.

```bash
make publish-demo-test-env
```

Before publishing make sure that you're loged in Gitlab container registry (use your Gitlab login username/password).

```bash
docker login registry.gitlab.com
```
