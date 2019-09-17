# Accelerated Text Front-End

The single-page application for editing _document plans_.

## Usage:

### Dependencies

* You should have NPM installed in your path. Get it from https://nodejs.org .
* You should have Accelerated Text back-end services running (see [Environment][#Environment] below).

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


##  Development

The app uses [Preact][Preact] v10.x, [Apollo GraphQL][Apollo] client and [Google Blockly][Blockly].

It is built with [Babel][Babel] and [Webpack][Webpack]. Tests are run with [AVA test runner][AVA].

You can start exploring the application from _./packages/webpack.config.js_ and _./packages/app/start-in-browser.js_ file.

Also don't forget to open DeveloperTools in your browser.

### Directory structure

* `./`  The root directory contains configuration files
    * `./assets/`       Assets include images and link to the pre-compiled [Google Blockly][Blockly] libraries.
    * `./dist/`         The directory for compiled files.
    * `./node_modules/` Dependencies.
    * `./packages/`     Source code for the app.

#### ./packages

The application is split into tens of smaller NPM-like packages with modules which depend on each other.

Some important packages:

* **accelerated-text**:     main view Component and global application state.
* **app**:                  global CSS styles and the script that runs the app in a HTML page.
* **blockly-helpers**:      some utility functions for [Blockly][Blockly].
* **graphql**:              queries, mutations, fragments, set-up script and some utility functions.
* **inject-blockly**:       a package that lets use [Blockly][Blockly] as a dependency instead of setting it up globally.
* **nlg-blocks**:           custom [Blockly][Blockly] blocks used in the app.
* **nlg-workspace**:        [Blockly][Blockly] Workspace wrapper Component.
* **plan-editor**:          UI used to edit document plans. Also the right _Sidebar_.
* **shortcuts**:            keyboard shortcut configuration and action implementation.
* **styles**:               global [Sass][Sass] variables and mixins.
* **tests**:                tests, data generators, [AVA macros][AVA_Macros].
* **webpack**:              [Webpack][Webpack] configuration.








[AVA]:          https://github.com/avajs/ava
[AVA_Macros]:   https://github.com/avajs/ava/blob/master/docs/01-writing-tests.md#reusing-test-logic-through-macros
[Apollo]:       https://www.apollographql.com/docs/react/
[Babel]:        https://babeljs.io/
[Blockly]:      https://developers.google.com/blockly/
[Preact]:       https://preactjs.com/
[Webpack]:      https://webpack.js.org/
