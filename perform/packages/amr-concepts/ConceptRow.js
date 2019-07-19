import classnames               from 'classnames';
import { h }                    from 'preact';

import DragInBlock              from '../drag-in-blocks/DragInBlock';
import { QA }                   from '../tests/constants';

import getBlockType             from './get-block-type';
import S                        from './ConceptRow.sass';


export default ({ concept }) =>
    <tr className={ classnames( S.className, QA.DICTIONARY_ITEM ) }>
        <td className={ S.dragInBlock }>
            { concept &&
                <DragInBlock
                    color={ S.dragInColor }
                    type={ getBlockType( concept ) }
                    width={ 36 }
                />
            }
        </td>
        <td
            children={ concept ? concept.label : '' }
            className={ classnames( S.label, QA.CONCEPT_LABEL ) }
        />
        <td
            children={ concept.helpText }
        />
    </tr>;
