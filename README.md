
<img style="float: none; width: 200px; overflow: hidden" alt="Accelerated Text" src="docs/assets/accelerated-text-logo.svg"/>
<img style="width: 80px; float: right" alt="TokenMill" src="docs/assets/tokenmill-logo.svg"/>

<p style="font-size:120%" align="center">
  <a href="#key-features">Key Features</a> • 
  <a href="#usage">Usage</a> • 
  <a href="#getting-help">Getting Help</a> • 
  <a href="#developing">Developing</a> • 
  <a href="#license">License</a> •
</p>

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


*A picture is worth a thousand words.* Or is it? The data you have needs to have a description, an interpretation and a story. 
**Accelerated Text** is a text generation tool which takes in your data and produces texts about it.

![Accelerated Text Preview](docs/assets/preview.gif)


Accelerated Text will use your data: 
* descriptions of your business metrics, 
* metadata describing interactions with your customers, 
* product descriptions, 
* financial metrics
and it will generate text to be used in reports, e-commerce platforms or your customer support system.

Accelerated Text provides a user friendly GUI to define the logic and communication goals of the document to be produced. We call it a **Document Plan**.  Text generation back end executes this plan connecting it with your data and producing multiple variations of the text exactly matching what you intended to communicate to your readers.


⚠ Data 2 Text pipeline image ending with animated gif which shows how text changes

[Your Data (grid)] -> [Accelerate Text] -> [GIF Result end app (shop?)]

## Key Features

* **Document plans** to define what needs to be said about your data.
* **Text structure variations** to provide richer reading experience going beyond rigid template generated text.
* **Vocabulary control** to match the language style of each of your reader groups.
* **In build rule engine** to allow you to state different facts based on the values of your data.
* **Life preview** of generated text.
* **Fully integrated** document plan definition GUI which allows you to import data samples, define document flow and see the results all in one place.


## Getting Started

### Running

#### Demo

Try out *Accelerated Text* at our [Demo](http://demo.acceleratedtext.org/) server.

#### Docker

To run *Accelerated Text* via Docker, just type

```
docker run -d -p 3000:3000 --name accelerated-text accelerated-text/accelerated-text
```

#### From Source

If you want to start tinkering and run it based on the latest code in the repository, first make sure that you have the following dependencies installed:

* [make](https://www.gnu.org/software/make/)
* [npm](https://nodejs.org )

Then clone the project and run

```
make run
```

### Usage

⚠️ For a walkthrough on creating your first text generator, check out our -Getting Started- guide. A video?

## Getting Help

Send your questions to ??@acceleratedtext.org or join our slack channel at acceleratedtext.slack.com

If you'll submit an [Issue](github/issues) this will help everyone and you will be able to track the progress of us fixing it. In order to facilitate it please provide description of needed information for bug requests (like project version number, Docker version, etc.)


## Development

To get started with a development of the Accelerated Text, follow the instructions at our Developers Guides for [Front-end](front-end/README.md) and [Back-end](docs/README-back-end.md) developers.

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
