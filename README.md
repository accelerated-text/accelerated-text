<div >

  <div valign="middle" height="125">
    <img height="70" alt="Accelerated Text" src="docs/assets/accelerated-text-logo.png"/>
  </div>

  <a href="http://www.tokenmill.lt">
    <img src="docs/assets/tokenmill-logo.svg" height="125" align="right" />
  </a>
 
</div>



----

*A picture is worth a thousand words.* Or is it? 
Tables, charts, pictures are all useful in undestanding our data but often we need a description; a story to tell us what are we looking at. 
**Accelerated Text** is a natural language generation tool which allows you to define data descriptions and then generates multiple versions of those descriptions varying in wording and structure.


<div align="center"><a name="menu"></a>
  <h3>
    <a href="#about">
      About
    </a>
    <span> • </span>
    <a href="#philosophy">
      Philosophy
    </a>
    <span> • </span>
    <a href="#getting-started">
      Getting Started
    </a>
    <span> • </span>
    <a href="#usage">
      Usage
    </a>
    <span> • </span>
    <a href="#development">
      Development
    </a>
    <span> • </span>
    <a href="#getting-help">
      Getting Help
    </a>
  </h4>
</div>


<br>

<div align="center">
<img src="docs/assets/preview.gif" width="900"/>
</div>


## About

Accelerated Text can work with all sorts of data:

* descriptions of business metrics, 
* metadata describing interactions with the customers, 
* product attributes, 
* financial metrics.

Based on it will generate text to be used in business reports, e-commerce platforms or your customer support system.

Accelerated Text provides a web based **Document Plan** builder, where 
* the logical structure of the document is defined, 
* communication goals are expresed, 
* data usage within a text is defined.

Document Plans and the connected data are used by Accelerated Text's Natural Language Generation engine 
to produce multiple variations of the text exactly exactly expressing what was intended to bo communicated to the readers.

## Philosophy

> Whereof one cannot speak thereof one must be silent <br>
>   -- _Wittgenstein_

Natural language generation is a broad domain with applications in chat-bots, story generation, and data description to name a few. 
Accelerated Text focuses on applying NLG technology to solve your "data to text" needs.

Data descriptions require precision. 
For example, a text describing weather conditions can not invent things beyond what it was provided: Temperature: -1C, Humidity: 40%, Wind: 10km/h. 
A generated text can only state those facts. The expression of an individual fact - temperature - could vary. 
It could result in a "it is cold", or "it is just below freezing", or "-1C" but this fact will be stated because it is in the data. 
A "data to text" system is also not one to elaborate on a story about the serenity of the freezing lake - again, it was not in the supplied data.

Accelerated Text follows the principle of this strict adherence to the data-bound text generation. 
Via its user interface it provides instruments to define how the data should be translated into descriptive text. 
This description - a document plan - is executed by its natural language generation engine to produce texts that vary in structure and wording but are always and only about the data provided.

## Key Features

* **Document plan** editor to define what needs to be said about the data.
* **Data samples** can be uploaded as CSV files to be used when building Document Plans.
* **Text structure variations** to provide richer reading experience going beyond rigid template generated text.
* **Vocabulary control** to match the language style of each of your reader groups.
* **Build-in rule engine** to allow the control of what is said based on the different values of the data points.
* **Live preview** to see variations of generated text.


## Getting Started

### Running

If you want to start tinkering and run it based on the latest code in the repository, first make sure that you have [make](https://www.gnu.org/software/make/) installed 

Then clone the project and run

```
make run-dev-env
```

After running this command the front-end will be availabe at the http://localhost:8080

The generation back-end API is at http://localhost:3001

### Usage

#### Create Document Plan

Follow the step by step guide bellow to create a very simple document plan which
generates book authorship sentences.


| View | Step |
| ------ | ------ |
| <img src="docs/assets/create-plan.png" width="300"/> | Firstly a new document plan has to be created. The application starts with a _Create Plan_ button in its workspace. |
| <img src="docs/assets/empty-doc-plan.png" width="200"/> | You get an initial empty plan. |
| <img src="docs/assets/csv-file-selection.png" width="200"/> | You'll need to select a CSV file to provide data for the natural language generation. Select a _books.csv_ file. |
| <img src="docs/assets/amr.png" width="300"/> | The central part of the plan is the _Abstract Meaning Representation_ element which defines the message to be communicated. Select _Author_ from the AMR section. |
| <img src="docs/assets/search-authors.png" width="300"/> | Then we need to select from where in our book store data we'll have the _Author_ field. |
| <img src="docs/assets/search-title.png" width="300"/> | Same for _Title_ field. |
| <img src="docs/assets/complete-plan.png" width="300"/> | That's it, the plan is ready and should look like in the picture to the right. |
| <img src="docs/assets/generation-results.png" width="300"/> | _Text Analysis_ section shows text variations generated by the natural language generation engine. |

#### GraphQL API use

In previous section we created a simple document plan. Here we will use Accelerated Text GraphQL API to fetch the text for the book items. In order to get the generated text, two bits of information need to be passed to the NLG backend: document plan identifier, and data item identifier for which the text will be generated.

If Accelerated Text was started as described in [Running](#running) section, then GraphQL endpoint is accessible at `http://localhost:3001/_graphql` endpoint. CURL will be used to illustrate the calls to the back end.

First lets get registered document plans:

```
curl -X POST -H "Content-Type: application/json" \
  --data '{ "query": "{documentPlans(offset:0 limit:10){items{id uid name dataSampleId dataSampleRow createdAt updatedAt updateCount} offset limit totalCount}}" }' \
  http://localhost:3001/_graphql
```

This will return a list of document plans:

```
  {:documentPlans
   {:limit 10,
    :offset 0,
    :totalCount 1,
    :items
    [{:updatedAt 1570951531,
      :uid "a7c31454-7f1b-4653-b14c-e2685793c110",
      :name "Book Store",
      :createdAt 1570951486,
      :dataSampleId "example-user/books.csv",
      :id "0ecdbada-dbbf-4b12-b1cb-cd6571181248",
      :dataSampleRow 0,
      :updateCount 8}]}}}}
```

The `id` field gives document plan id, and the `dataSampleId` field specifies which data to use. 

```
{"documentPlanId":"0ecdbada-dbbf-4b12-b1cb-cd6571181248",
 "readerFlagValues":{},
 "dataId":"example-user/books.csv"}
```
With this, a second call has to be made to get the results identifier for actual sentence polling. Polling is used because text is not generated right away, NLG process for a more complcated plans can take some time.

```
curl -XPOST -H "Content-Type: application/json" \ 
http://localhost:3001/nlg -d '{"documentPlanId":"0ecdbada-dbbf-4b12-b1cb-cd6571181248","readerFlagValues":{},"dataId":"example-user/books.csv"}'
```

A result id is returned:

```
{"resultId" : "6f26099d-429d-41e9-9800-83ab58c59ddd"}
```

Whith this a final request can be made to fetch the results. Note that it can be done repeatedly with high performance, since the text generation is not happening at this stage.

```
curl -XGET -H "Content-Type: application/json" http://localhost:3001/nlg/6f26099d-429d-41e9-9800-83ab58c59ddd
```

You should get generated text with annotations (data is truncated):

```
{
   "offset":0,
   "totalCount":5,
   "ready":true,
   "variants":[
      {
         "type":"ANNOTATED_TEXT",
         "id":"ae9a1d60-4aa6-49da-9738-480243a5095b",
         "children":[
            {
               "type":"PARAGRAPH",
               "id":"ab8b650a-8774-4992-8a56-fe8d01f74097",
               "children":[
                  {
                     "type":"SENTENCE",
                     "id":"5cda3e9f-8fad-4b69-a0a3-f9f5e9a19465",
                     "children":[
                        {
                           "type":"WORD",
                           "id":"db5c71ec-f893-4406-8a3f-e91ca6aa08dc",
                           "text":"Building"
                        },
                        {
                           "type":"WORD",
                           "id":"84e164c7-1fd0-4f18-b120-8a4f7563741b",
                           "text":"Search"
                        },
                        {
                           "type":"WORD",
                           "id":"2e54b4b1-ed52-4689-8f5f-0a06ec8a35b5",
                           "text":"Applications"
                        }
                        ...
                        {
                           "type":"PUNCTUATION",
                           "id":"5710b009-4ad2-4b6d-b738-945cb576c1fc",
                           "text":"."
                        }
                     ]
                  }
               ]
            }
         ]
      },
     ...
}
```

#### Clojure API use

Accelerated Text UI helps with creating document plan and testing it with sample data. 
Accelerated Text's generation functionality can be used directly from the Clojure code.

Lets say you have a book data limited to the author and the book title:

| title                       | author        |
| -----                       | ------        |
| Frankenstein                | M. W. Shelley |
| Dracula                     | Bram Stoker   |
| The Island of Doctor Moreau | H.G. Wells    |

When working via UI this data needs to be uploaded as the CSV. To use it in the code we'll have to represent as a Clojure map. 

```
(def data
  [{:title "Frankenstein"
    :author "M. W. Shelley"}
   {:title "Dracula"
    :author "Bram Stoker"}
   {:title "The Island of Doctor Moreau"
    :author "H.G. Wells"}])
```

Second component needed for generation is the plan itself. In UI it has a nice representation in visual blocks, and is persisted in the structure like this:

```
(def document-plan 
  {:type "Document-plan"
  :segments
  [{:type "Segment"
    :textType "description"
    :children
    [{:type "AMR"
      :conceptId "author"
      :dictionaryItem {:itemId "written"
                        :name "written"
                        :type "Dictionary-item"}
      :roles [{:name "Agent"
                :children [{:type "Thematic-role"
                            :children [{:type "Cell"
                                        :name "author"}]}]}
              {:name "co-Agent"
                :children [{:type "Thematic-role"
                            :children [{:type "Cell"
                                        :name "title"}]}]}]}]}]})
```

With those two in place we can generate the text:

```
(api.nlg.generator.planner-ng/render-dp document-plan data {})
=>
("The Island of Doctor Moreau is written by H.G. Wells"))
("Frankenstein is done by M. W. Shelley."
 "Dracula is done by Bram Stoker."
 "The Island of Doctor Moreau is written by H.G. Wells.")
```


## Development

To get started with a development environment for Accelerated Text please follow the instructions in our developer's guides 
for the [front-end](front-end/README.md) and the [text generation engine](core/README.md).

## Getting Help

If you have any questions, do not hesitate asking us at accelerated-text@tokenmill.lt

If you'll submit an *Issue* this will help everyone and you will be able to track the progress of us fixing it. 
In order to facilitate it please provide description of needed information for bug requests (like project version number, Docker version, etc.)


## License 

Copyright &copy; 2019 [TokenMill UAB](http://www.tokenmill.lt).

Distributed under the The Apache License, Version 2.0.
