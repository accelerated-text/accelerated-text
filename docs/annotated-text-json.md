#   Annotated Text JSON (ATJ) format

The format helps describe pieces of text (usualy generated from a _Document Plan_).

A piece of text consists of DOM-like _Element_ tree starting with a _Annotated Text Element_ at the root.

_*Note:* this doc uses [Yaml] instead of [JSON] in code examples to increase readability. JSON example file is [here](variants-example.json)._

####    Example

```yaml
id:                 text-uuid-0001
type:               ANNOTATED_TEXT
plan_id:            plan-uuid-0002
plan_version:       version-uuid-0003
annotations:
    -
        id:         annotation-uuid-0004
        type:       PROPERTY_LIST
references:
    -
        id:         reference-uuid-0005
        type:       CALL_TO_ACTION
children:
    -
        id:         element-uuid-0006
        type:       PARAGRAPH
        implements: blockly-uuid-0007
        children:
            -
                id:         element-uuid-0008
                type:       SENTENCE
                implements: blockly-uuid-0009
                children:
                    -
                        id:             element-uuid-000a
                        type:           SYNONYM_SET
                        synonyms:       [ "Buy", "Grab" ]
                        text:           "Buy"
                        references:
                            - reference-uuid-0005
                    -
                        id:             element-uuid-000b
                        type:           ATTRIBUTE
                        implements:     blockly-uuid-000c
                        attribute_name: color
                        text:           red
                        annotations:
                            - annotation-uuid-0004
                    -
                        id:             element-uuid-000d
                        type:           ATTRIBUTE
                        implements:     blockly-uuid-000e
                        attribute_name: material
                        text:           cotton
                        annotations:
                            - annotation-uuid-0004
                    -
                        id:             element-uuid-000f
                        type:           WORD
                        text:           t-shirt
                        part_of_speech: noun
                    -
                        id:             element-uuid-0010
                        type:           PUNCTUATION
                        text:           "!"
                        references:
                            - reference-uuid-0005
```

## Elements

There are three groups of elements: _Container Elements_, _Text Elements_ and _Annotations & References_.

At the root of the tree there should be one _Annotated Text Element_ which is a type of a _Container Element_.

All Element objects:

*   must have an `id` property unique within the piece of text,
*   must have a `type` property (an UPPER_CASE _string_),
*   may include other type-dependent or arbitrary properties.
    * In the case of an unknown property, the program using the Element should ignore the property and not report any errors to the user.

### Annotations & References

*   Describe relationships between words.
*   Can only be attached to the `ANNOTATED_TEXT` Element at the root.
*   Should be referenced by the _Text Elements_ participating in the relationship.

####    Synthetic example:
```yaml
id:                 reference-uuid-0001
type:               SAME_PERSON
```

### Container Elements

*   Must have a (non-empty) `children` _array_ containing _Container Elements_ and/or _Text Elements_.

Example types: `ANNOTATED_TEXT`, `EMPHASIS`, `PARAGRAPH`, `SENTENCE`.

### Annotated Text Element

*   Must have type `ANNOTATED_TEXT`.
*   Must reference the _Document Plan_ that created it.
*   Should include _arrays_ of _Annotations & References_ used by child Elements.

####    Minimal Valid Example
```yaml
id:                 text-uuid-0001
type:               ANNOTATED_TEXT
plan_id:            plan-uuid-0002
plan_version:       plan-uuid-0003
annotations:        []
references:         []
children:           []
```

### Text Elements

*   Must have a non-empty `text` _string_ property.
*   Must not have children.
*   Should list the relevant annotations and references.

Example types: `ATTRIBUTE`, `PUNCTUATION`, `SYNONYM_SET`, `WORD`.

####    Example
```yaml
id:                 element-uuid-0001
type:               WORD
text:               dog
annotations:
    - annotation-uuid-0002
    - annotation-uuid-0003
references:
    - reference-uuid-0004
    - reference-uuid-0005
```

### Reserved property names

These properties are reserved for future use and should NOT be used for _Element_ types without a consultation:

*   attributes
*   class
*   classList
*   className
*   data
*   dataset
*   href
*   innerHTML
*   innerText
*   name
*   outerHTML
*   parent
*   rel
*   src
*   title
*   value

*Note: this rule applies to all forms of the name (e.g. classList, class_list, etc.).*

[JSON]:             https://json.org/
[Yaml]:             https://yaml.org/
