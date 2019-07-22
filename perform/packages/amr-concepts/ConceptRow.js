import classnames               from 'classnames';
import { h }                    from 'preact';

import DragInBlock              from '../drag-in-blocks/DragInBlock';
import { QA }                   from '../tests/constants';

import S                        from './ConceptRow.sass';


export default ({ concept }) =>
    <tr className={ classnames( S.className, QA.CONCEPT ) }>
        <td className={ S.dragInBlock }>
            { concept &&
                <DragInBlock
                    color={ S.dragInColor }
                    mutation={{
                        concept_label:  concept.label,
                        roles:          JSON.stringify( concept.roles ),
                    }}
                    type="AMR"
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
