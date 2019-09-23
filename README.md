<p align="center">
  <img alt="Accelerated Text" src="docs/assets/accelerated-text-logo.svg" width="400"/>
</p>

# Accelerated Text

Accelerated Text is a text generation tool which takes in your data and produces texts describing it.

#### [Key Features](#key-features) • [Usage](#usage) • [Getting Help](#getting-help) • [Contributing](#contributing) • [License](#license) • [Demo](http://demo.acceleratedtext.org/)

![Accelerated Text Preview](docs/assets/preview.gif)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Accelerated Text will use your data: 
* descriptions of your business metrics, 
* metadata describing interactions with your customers, 
* product descriptions, 
* financial metrics
and it will generate text to be used in reports, e-commerce platforms or your customer support system.

Accelerated Text provides a user friendly GUI to define the logic and communication goals of the document to be produced. We call it a **Document Plan**.  Text generation back end executes this plan connecting it with your data and producing multiple variations of the text exactly matching what you intended to communicate to your readers.

## Key Features

* **Document plans** to define what needs to be said about your data.
* **Text structure variations** to provide richer reading experience going beyond rigid template generated text.
* **Vocabulary control** to match the language style of each of your reader groups.
* **In build rule engine** to allow you to state different facts based on the values of your data.
* **Life preview** of generated text.
* **Fully integrated** document plan definition GUI which allows you to import data samples, define document flow and see the results all in one place.

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

## Getting Started

⚠️ For a walkthrough on creating your first text generator, check out our -Getting Started- guide. A video?

## Getting Help

Send your questions to ??@acceleratedtext.org or join our slack channel at acceleratedtext.slack.com

If you'll submit an [Issue](github/issues) this will help everyone and you will be able to track the progress of us fixing it. In order to facilitate it please provide description of needed information for bug requests (like project version number, Docker version, etc.)


## Contributing

We accept pull requests with your features or bug fixes.

To get started with a development installation of the Accelerated Text, follow the instructions at our Developers Guides for [Front-end](front-end/README.md) and [Back-end](docs/README-back-end.md) developers.

Then take a look at our [Contribution Guide](docs/contributing.md) for information about our process and where you can fit in!

Talk to other contributors in our Chat room.

## License 

Copyright &copy; 2019 [TokenMill UAB](http://www.tokenmill.lt).

Distributed under the The Apache License, Version 2.0.


## TODO

This goes somewhere. Back end doc?

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
