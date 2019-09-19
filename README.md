![Accelerated Text](perform/assets/accelerated-text-logo.svg)

# Accelerated Text

Accelerated Text is a text generation tool which takes in your data and produces texts describing it.


[Key Features](#Key_Features) • [Usage](#Usage) • [Getting Help](#Getting_Help) • [Contributing](#Contributing) • [License](#License) • [Demo](#Demo)

![Accelerated Text](docs/assets/quick_demo.mp4)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Provide Accelerated Text with your data: descriptions of your business metrics, metadata describing interactions with your customers, product descriptions, financial data and get back text to be used in report generation, e-commerce platform or your customer support system.

Accelerated Text provides a user friendly GUI to define the logic and communication goals of the document to be produced. We call it a *Document Plan*.  Text generation back end executes this plan connecting it with your data and producing multiple variations of the text exactly matching what you intended to communicate to your readers.

## Key Features

* *Document plans* to define what needs to be said about your data.
* *Text structure variations* to provide richer reading experience going beyond rigid template generated text.
* *Vocabulary control* to match the language style of each of your reader groups.
* *In build rule engine* to allow you to state different facts based on the values of your data.
* *Life preview* of generated text.
* *Fully integrated* document plan definition GUI which allows you to import data samples, define document flow and see the results all in one place.

## Usage

⚠️ should answer: How do I test/evaluate it?
    * Should have a demo link.
    * Should have dependencies (links) + 1 shell command to run it locally.
    * May have an alternative method to run it.

### Dependencies

You should have NPM installed in your path. Get it from https://nodejs.org .

### Running

```bash
make run
```

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

⚠️ For a walkthrough on creating your first text generator, check out our -Getting Started- guide.

# Getting Help

* Where to submit issues
* FAQ
* Where to ask questions (chat?)

# Contributing
⚠️  

To get started with a development installation of the AccText, follow the instructions at our Developers Guide.

Then take a look at our Contribution Guide for information about our process and where you can fit in!

Talk to other contributors in our Chat room.

# License 

⚠️ the section should contain legal stuff:
    * copyright,
    * owners name and contact.
