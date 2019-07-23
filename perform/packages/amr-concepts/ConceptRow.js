import classnames               from 'classnames';
import { h }                    from 'preact';

import ExpandableOverflow       from '../expandable-overflow/ExpandableOverflow';
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
        <td className={ S.helpText }>
            <ExpandableOverflow
                children={ concept.helpText }
                className={ S.helpTextContainer }
                expandedClassName={ S.expandedHelp }
            />
        </td>
    </tr>;
