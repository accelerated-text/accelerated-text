#   Annotated Text JSON (ATJ) format

The format helps describe pieces of text generated from a _Document Plan_.

A piece of text consists of DOM-like _Element_ tree starting with a root _Element_.

_*Note:* this doc uses [Yaml] instead of [JSON] in code examples to increase readability._

####    Example

```yaml
id:                 element-uuid-0001
type:               CONTAINER
plan_id:            plan-uuid-0002
plan_version:       version-uuid-0003
children:
    -
        id:         element-uuid-0004
        type:       PARAGRAPH
        implements: blockly-uuid-0005
        children:
            -
                id:         element-uuid-0006
                type:       SENTENCE
                implements: blockly-uuid-0007
                children:
                    -
                        id:         element-uuid-0008
                        type:       WORD
                        implements: null
                        text:       "An"
                    -
                        id:         element-uuid-0009
                        type:       SYNONYM_SET
                        implements: null
                        text:       "eerie"
                        synonyms:   []  # TBD
                    -
                        id:         element-uuid-000a
                        type:       ATTRIBUTE
                        implements: blockly-uuid-000b
                        text:       "yellow"
                    -
                        id:         element-uuid-000c
                        type:       PUNCTUATION
                        text:       "..."
```

## Element

There are two types of elements: _Container Elements_ and _Text Elements_.

### Container Element

_Container Elements_ have a non-empty `children` _array_.

Example types: `CONTAINER`, `EMPHASIS`, `PARAGRAPH`, `SENTENCE`.

### Text Element

_Text Elements_ have a non-empty `text` _string_.

Example types: `ATTRIBUTE`, `PUNCTUATION`, `SYNONYM_SET`, `WORD`.

### JSON structure

Both types of elements are represented by a [JSON] object with some of these fields:

```yaml
id:                 element-uuid-0001   # required
type:               paragraph           # required

text:               plain-text          # one of these required
children:           [...]               # one of these required

plan_id:            plan-uuid-0002      # optional
plan_version:       version-uuid-0003   # optional
implements:         blockly-uuid-0004   # optionsl

class:              string              # reserved for future use
classList:          [...]               # reserved for future use
className:          string              # reserved for future use
style:              string              # reserved for future use
```
