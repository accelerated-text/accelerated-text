# Accelerated Text Front-End

The single-page application for editing _document plans_.

## Usage:

### Dependencies

* You should have NPM installed in your path. Get it from https://nodejs.org .
* You should have Accelerated Text back-end services running (see [Environment] below).

### Running

```bash
npm install
npm start
```

You should see this message in the output after a successful compile:

```
ℹ ｢wdm｣: Compiled successfully.
```

Open your browser at [localhost:8080](http://localhost:8080/).

### Environment

The application expects back-end services running on ports `3001`, `8081`, `8090` and an S3 bucket URL for uploading CSV data files.

You can override the URLs via _environment variables_. E.g.:

```
GRAPHQL_URL=https://example.org/_graphql npm start
```

See file _.env.defaults_ for the variables you can use.
