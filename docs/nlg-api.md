#   NLG APIs

##  Generate document

*   The API accepts a Text Generator id and paging variables.
*   The API responds with an iterable list of text examples in Document JSON format.

####    API example

Request:

```yaml
POST:   /api/gen-document

generatorId:    generator-uuid-0001
offset:         0
limit:          10
```

Response:

```yaml
offset:         0
totalCount:     1234567
documents:
    -
        id:                 doc-uuid-0002
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
                                type:       syncset
                                implements: null
                                text:       "

                            -
                                id:         element-uuid-0008
                                type:       attribute
                                implements: blockly-uuid-0009
                                text:       "yellow"
                            -

