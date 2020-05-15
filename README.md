<div >

  <div valign="middle" height="125">
    <img height="70" alt="Accelerated Text" src="docs/assets/accelerated-text-logo.png"/>
  </div>

  <a href="http://www.tokenmill.lt">
    <img src="docs/assets/tokenmill-logo.svg" height="125" align="right" />
  </a>
 
</div>



---

*A picture is worth a thousand words.* Or is it? 
Tables, charts, pictures are all useful in understanding our data but often we need a description – a story to tell us what are we looking at. 
**Accelerated Text** is a natural language generation tool which allows you to define data descriptions and then generates multiple versions of those descriptions varying in wording and structure.


<div align="center"><a name="menu"></a>
  <h3>
    <a href="https://accelerated-text.readthedocs.io/">
      Docs
    </a>
    <span> • </span>
    <a href="#about">
      About
    </a>
    <span> • </span>
    <a href="#philosophy">
      Philosophy
    </a>
    <span> • </span>
    <a href="#key-features">
      Key Features
    </a>
    <span> • </span>    
    <a href="#get-started">
      Get Started
    </a>    
    <span> • </span>
    <a href="#demo">
      Demo
    </a>
    <span> • </span>
    <a href="#development">
      Development
    </a>
    <span> • </span>
    <a href="#contact-us">
      Contact Us
    </a>
  </h4>
</div>


<br>

<div align="center">
<img src="docs/assets/preview.gif" width="900"/>
</div>


## About

Accelerated Text can work with all sorts of data:

* descriptions of business metrics
* customer interaction data
* product attributes
* financial metrics

With Accelerated Text you can use such data to generate text for your business reports, your e-commerce platform or your customer support system.

Accelerated Text provides a web based **Document Plan** builder, where: 
* the logical structure of the document is defined
* communication goals are expressed
* data usage within text is defined

Document Plans and the connected data are used by Accelerated Text's Natural Language Generation engine 
to produce multiple variations of text exactly expressing what was intended to be communicated to the readers.

## Philosophy

> Whereof one cannot speak thereof one must be silent <br>
>   -- _Wittgenstein_

Natural language generation is a broad domain with applications in chat-bots, story generation, and data descriptions to name a few. 
Accelerated Text focuses on applying NLG technology to solve your *data to text* needs.

Data descriptions require precision. 
For example, generated text describing weather conditions should not contain things beyond those provided in the initial data – temperature: -1C, humidity: 40%, wind: 10km/h. 
Despite this, the expression of an individual fact – temperature – could vary. It could result in "it is cold", or "it is just below freezing", or "-1C", but this fact *will* be stated because it is present in the data. 
A data to text system is also not the one to elaborate on a story adding something about the serenity of some freezing lake – again, it was not in the supplied data.

Accelerated Text follows the principle of this strict adherence to the data-bound text generation. 
Via its user interface it provides instruments to define how the data should be translated into a descriptive text. 
This description – a document plan – is executed by natural language generation engine to produce texts that vary in structure and wording but are always and only about the data provided.

## Key Features

* **Document plan** editor to define what needs to be said about the data.
* **Data samples** can be uploaded as CSV files to be used when building Document Plans.
* **Text structure variations** to provide richer reading experience going beyond rigid template generated text.
* **Language and vocabulary  control** to match each of your reader groups.
* **Build-in rule engine** to allow the control of what is said based on the different values of data points.
* **Live preview** to see variations of generated text.

## Get Started

If you want to start tinkering and run it based on the latest code in the repository, first make sure that you have [make](https://www.gnu.org/software/make/) and [docker-compose](https://docs.docker.com/compose/install/) installed, then clone the project and run

```
make run-dev-env
```

After running this command the document plan editor will be availabe at _http://localhost:8080_, while AMR and DLG editors will be reachable via _http://localhost:8080/amr/_ and _http://localhost:8080/dlg/_ respectively.

For more detailed description of text generation workflow visit the [Documentation](https://accelerated-text.readthedocs.io/).

## Demo

For a demonstration of how Accelerated Text can be used to provide descriptions for various items in an e-commerce platform (https://www.reactioncommerce.com/) please check the following repository: https://github.com/tokenmill/reaction-acc-text-demo.

## Development

To get started with a development environment for Accelerated Text please follow the instructions in our developer's guides 
for the [front-end](front-end/README.md), [api](api/README.md) and the [text generation engine](core/README.md).

## Contact Us

If you have any questions, do not hesitate asking us at accelerated-text@tokenmill.ai

If you'll submit an *Issue* this will help everyone and you will be able to track the progress of us fixing it. 
In order to facilitate it please provide description of needed information for bug requests (like project version number, Docker version, etc.)


## License 

Copyright &copy; 2019 [TokenMill UAB](http://www.tokenmill.ai).

Distributed under the The Apache License, Version 2.0.
