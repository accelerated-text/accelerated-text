#   Blockly Workspace JSON (BWJ) format

The format helps describe user-created programs in a [Blockly] _Workspace_.

_*Note:* this doc uses [Yaml] instead of [JSON] in code examples to increase readability._

##  Workspace

A _Workspace_ in [Blockly] is the UI area where a user can drag, drop and connect Blockly blocks to build a program. It has its own [API][Workspace API].

A _workspace_ in BWJ has an _array_ of top-level _BWJ blocks_:

```yaml
blocks:     [...]
```

##  BWJ Block

A _block_ JSON object tries to stay close to the XML format used by Blockly.

It should inlcude only these properties:

```yaml
id:         uuid                # required
type:       blockly-block-type  # required, defined in a list
x:          123                 # optional
y:          456                 # optional
mutation:                       # optional
    name:   mutation-value
    ...
fields:                         # optional
    name:   field-value
    ...
statements:                     # optional
    name:   block-array
    ...
values:                         # optional
    name:   block-value
    ...
```

##  Mutation-value

May be any [JSON] value. Preferrably _string_, _number_, `true`, `false` or `null`.

##  Field-value

May be _string_, _number_, `true`, `false` or `null`.

##  Block-array

An _array_ of _BWJ blocks_.

##  Block-value

A _value_ may be a _BWJ block_ or an _array_ of _BWJ blocks_.

```yaml
...
values:
    text:           "A word"
...
```

In case of an _array_, the parent _BWJ block_ should include a mutation field specifying the _count_ of the elements in the array:

```yaml
...
mutation:
    children_count: 2
values:
    children:
        -
            id:         uuid-0001
            type:       all-attributes
        -
            id:         uuid-0002
            type:       string
            fields:
                text:   "user entered string"
...
```


[Blockly]:          https://developers.google.com/blockly/
[JSON]:             https://json.org/
[Yaml]:             https://yaml.org/
[Workspace API]:    https://developers.google.com/blockly/reference/js/Blockly.Workspace
