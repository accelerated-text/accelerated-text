import { h }                    from 'preact';

import BlockComponent           from '../block-component/BlockComponent';
import AmrBlock                 from '../nlg-blocks/AMR';
import DictionaryItemBlock      from '../nlg-blocks/Dictionary-item';
import DragInBlock              from '../drag-in-blocks/DragInBlock';

import S                        from './ConceptRow.sass';

/// TODO: may need optimization

export default ({ className, concept }) =>
    <DragInBlock
        className={ className }
        color={ S.dragInColor }
        mutation={{
            concept_id:     concept.id,
            concept_label:  concept.label,
            roles:          JSON.stringify( concept.roles ),
        }}
        type={ AmrBlock.type }
        values={{
            dictionaryItem: BlockComponent({
                mutation: {
                    id:     concept.dictionaryItem.id,
                    name:   concept.dictionaryItem.name,
                },
                type:       DictionaryItemBlock.type,
            }),
        }}
        width={ 36 }
    />;
