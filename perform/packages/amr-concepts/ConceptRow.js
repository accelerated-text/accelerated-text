import classnames               from 'classnames';
import { h }                    from 'preact';

import { QA }                   from '../tests/constants';

import ConceptDragIn            from './ConceptDragInBlock';
import S                        from './ConceptRow.sass';


export default ({ concept }) =>
    <tr className={ classnames( S.className, QA.CONCEPT ) }>
        <td className={ S.dragInBlock }>
            { concept &&
                <ConceptDragIn concept={ concept } />
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
