import { h }                    from 'preact';

import BlockComponent           from '../block-component/BlockComponent';
import AmrBlock                 from '../nlg-blocks/AMR';
import DictionaryItemBlock      from '../nlg-blocks/Dictionary-item';
import DragInBlock              from '../drag-in-blocks/DragInBlock';

/// TODO: may need optimization

export default ({ className, concept }) =>
    <DragInBlock
        className={ className }
        block={ AmrBlock }
        mutation={{
            concept_id:     concept.id,
            concept_label:  concept.label,
            roles:          JSON.stringify( concept.roles ),
        }}
        values={{
            dictionaryItem: BlockComponent({
                mutation: {
                    id:     concept.dictionaryItem.id,
                    name:   concept.dictionaryItem.name,
                },
                type:       DictionaryItemBlock.type,
            }),
        }}
    />;
