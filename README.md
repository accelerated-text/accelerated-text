# Accelerated Text

Accelerated Text is a text generation tool which takes in your data and produces texts descring it. Be it descriptions of your business metrics to generate reports, product descriptions for your e-commerce site, or elaborate customer support messages for your chat bot.

Accelerated Text provides a user friendly GUI to define the logics and communication goals of the document to be produced. We call it a *Document Plan*.  Text generation back end executes this plan connecting it with your data and producing multiple variations of the text exactly matching what you inteded to communicate to your readers.

⚠️ the section should answer: What is this?
    * Needs screenshot.
    * Should have one description sentence. The current one seems too vague.

Accelerated Text helps in building domain specific texts as well as constructing templates.

## Key Features

## Usage

⚠️ should answer: How do I test/evaluate it?
    * Check if title is appropriate.
    * Should have a demo link.
    * Should have dependencies (links) + 1 shell command to run it locally.
    * May have an alternative method to run it.

### Dependencies

You should have NPM installed in your path. Get it from https://nodejs.org .

### Running

```bash
make run
```


⚠️  the section should answer: What features are available
    * A bullet list would probably do.


⚠️ the section should answer: How do I get my bug fixes, or community support?
    * Link to Gitlab/GitHub issues page.
    * Description of needed information for bug requests (like project version number, Docker version, etc.)
    * Link to other support channels.


⚠️ the section should answer: How do I fix/change my local version?
    * Short overview of codebase structure with links to other Markdown files?
    * Directions how to submit PRs.


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

⚠️ the section should contain legal stuff:
    * copyright,
    * owners name and contact.
