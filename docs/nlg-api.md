#   NLG APIs

##  Generate documents

*   The API accepts a Text Generator id and paging variables.
*   The API responds with an iterable list of text examples in Document JSON format.

####    Request:

```yaml
# HTTP:
POST:           .../gen-documents
# Headers:
Content-Type:   application/json
# Body:
generatorId:    generator-uuid-0001
offset:         0
limit:          10
```

####    Response:

```yaml
# Headers:
Content-Type:   application/json
# Body:
offset:         0
totalCount:     1234567
documents:
    -
        id:                 doc-uuid-0002
        type:               document
        generatorId:        generator-uuid-0001
        children:
            -
                id:         element-uuid-0003
                type:       paragraph
                implements: blockly-uuid-0004
                children:
                    -
                        id:         element-uuid-0005
                        type:       sentence
                        implements: blockly-uuid-0006
                        children:
                            -
                                id:         element-uuid-0007
                                type:       word
                                implements: null
                                text:       "An"
                            -
                                id:         element-uuid-0008
                                type:       synonym-set
                                implements: null
                                text:       "eerie"
                                synonyms:   []  # TBD
                            -
                                id:         element-uuid-0008
                                type:       attribute
                                implements: blockly-uuid-0009
                                text:       "yellow"
                            -
                                id:         element-uuid-000a
                                type:       sentence-terminal # Such type would be useful for front-end.
                                text:       "."
```

##  Create a generator

*   The API accepts a Text Generator in Blockly JSON format (without an id property).
*   The API responds with a Text Generator in Blockly JSON format (including the assigned id value).

####    Request

```yaml
# HTTP:
POST:           .../generators/
# Headers:
Content-Type:   application/json
# Body:
blocks:
    -
        id:         blockly-uuid-000b
        type:       segment
        x:          123
        y:          456
        fields:
            goal:   description
        statements:
            -
                id:                 blockly-uuid-000c
                type:               all-words
                mutation:
                    children_count: 1
                values:
                    children:
                        -
                            id:         blockly-uuid-000d
                            type:       word
                            fields:
                                text:   huge
                        -
                            id:         blockly-uuid-000d
                            type:       attribute
                            fields:
                                name:   color
```

####    Response

```yaml
# HTTP:
POST:           .../generators/
# Headers:
Content-Type:   application/json
# Body:
id:                 generator-uuid-0001
blocks:
    -
        id:         blockly-uuid-000b
        type:       segment
        x:          123
        y:          456
        fields:
            goal:   description
        statements:
            -
                id:                 blockly-uuid-000c
                type:               all-words
                mutation:
                    children_count: 1
                values:
                    children:
                        -
                            id:         blockly-uuid-000d
                            type:       word
                            fields:
                                text:   huge
                        -
                            id:         blockly-uuid-000d
                            type:       attribute
                            fields:
                                name:   color
```

##  Update a generator

*   The API accepts a Text Generator in Blockly JSON format.
*   The API responds with the up-to-date version of the Text Generator in Blockly JSON format.

####    Request

```yaml
# HTTP:
PUT:            .../generators/generator-uuid-0001
# Headers:
Content-Type:   application/json
# Body:
id:             generator-uuid-0001
blocks:         [] # See "Create a generator" above.
```
