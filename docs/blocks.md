# Root blocks

These are the main blocks for document plans, AMRs and Operations. Only one of these blocks can exists in each plan, they cannot be deleted and are created automatically.

| Block | Name | Description |
| ------ | ------ | ------ |
| ![document-plan](assets/blocks/document-plan.png) | Document plan | Root block of document plan. |
| ![amr](assets/blocks/amr.png)                     | AMR           | Root block of AMR. |
| ![operation](assets/blocks/operation.png)         | Operation     | Root block of Operation. |

# Document blocks

Document blocks are blocks in Accelerated Text that get directly attached to root.

| Block | Name | Description |
| ------ | ------ | ------ |
| ![segment](assets/blocks/segment.png)           | Segment             | Text segment roughly represents a single paragraph of text and is available only in Document Plan Editor. Multiple items can be attached to this block. |
| ![frame](assets/blocks/frame.png)               | Frame               | Frame block is a single variant of text and is only available in AMR and DLG editors. Only single item can be attached to this block. |
| ![set-variable](assets/blocks/set-variable.png) | Variable definition | Defines value of specific variable. |

# Value blocks

Basic building blocks of document plans, AMRs and Operations.

| Block | Name | Description |
| ------ | ------ | ------ |
| ![quote](assets/blocks/quote.png)                                       | Quote                    | A text that may be changed anytime. |
| ![data](assets/blocks/data.png)                                         | Data cell                | Text from specific data row and column. Available in Data section of sidebar. |
| ![dictionary-item](assets/blocks/dictionary-item.png)                   | Dictionay item           | Word defined in dictionary. Available in Dictionary section of sidebar. |
| ![data-modifier](assets/blocks/data-modifier.png)                       | Data cell modifier       | Similar to data cell, with ability to attach another block. Modifies attached block. Available in Data section of sidebar. |
| ![dictionary-item-modifier](assets/blocks/dictionary-item-modifier.png) | Dictionary item modifier | Dictionary item that accepts a block to be modified. Available in Dictionary section of sidebar. |
| ![modifier](assets/blocks/modifier.png)                                 | Modifier                 | Accepts a block to upper position that modifies a block attached to lower position. |
| ![variable](assets/blocks/variable.png)                                 | Variable                 | Points to *variable definition block*. Undefined variables are treated as parameters for AMRs and Operations. |

# List blocks

| Block | Name | Description |
| ------ | ------ | ------ |
| ![sequence](assets/blocks/sequence.png)               | Sequence        | Accepts multiple blocks. |
| ![in-random-order](assets/blocks/in-random-order.png) | In random order | Accepts multiple blocks that get randomly shuffled. |
| ![one-of-synonyms](assets/blocks/a-synonym-from.png)  | A synonym from  | Produces variants for each attached block. Available in Document Plan Editor. |
| ![one-of](assets/blocks/one-of.png)                   | One of          | Works similar to *a synonym from block*. Available in AMR and DLG Editors. |

# Logic blocks

| Block | Name | Description |
| ------ | ------ | ------ |
| ![if](assets/blocks/if.png)               | If condition    | Conditional block. Can have multiple branches. |
| ![and](assets/blocks/and.png)             | AND             | True if both blocks are true. |
| ![or](assets/blocks/or.png)               | OR              | True if one of blocks are true. |
| ![either-or](assets/blocks/either-or.png) | XOR (Either Or) | True if one block is true and another is false. |
| ![not](assets/blocks/not.png)             | NOT             | True becomes false and false becomes true. |

# Checks

| Block | Name | Description |
| ------ | ------ | ------ |
| ![equal](assets/blocks/equal.png)               | Equality check     | May be changed to test inequality (≠). |
| ![greater-than](assets/blocks/greater-than.png) | Greater than check | Works with numbers. May be changed to test greater than or equal (>=), less than (<), less than or equal (<=) |
| ![is-in](assets/blocks/is-in.png)               | Is in check        | Checks if substring is included string. |
