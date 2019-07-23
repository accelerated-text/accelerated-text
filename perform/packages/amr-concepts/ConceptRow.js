import classnames               from 'classnames';
import { h }                    from 'preact';

import ExpandableOverflow       from '../expandable-overflow/ExpandableOverflow';
import { QA }                   from '../tests/constants';

import ConceptDragIn            from './ConceptDragInBlock';
import S                        from './ConceptRow.sass';


export default ({ concept }) =>
    <tr className={ classnames( S.className, QA.AMR_CONCEPT ) }>
        <td className={ S.dragInBlock }>
            { concept &&
                <ConceptDragIn
                    className={ QA.AMR_CONCEPT_DRAG_BLOCK }
                    concept={ concept }
                />
            }
        </td>
        <td
            children={ concept ? concept.label : '' }
            className={ classnames( S.label, QA.AMR_CONCEPT_LABEL ) }
        />
        <td className={ classnames( S.helpText, QA.AMR_CONCEPT_HELP ) }>
            <ExpandableOverflow
                children={ concept.helpText }
                className={ S.helpTextContainer }
                expandedClassName={ S.expandedHelp }
            />
        </td>
    </tr>;
