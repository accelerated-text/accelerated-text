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
        <td className={ S.helpText }>
            <ExpandableOverflow
                children={ concept.helpText }
                className={ classnames( S.helpTextContainer, QA.AMR_CONCEPT_HELP ) }
                expandedClassName={ S.expandedHelp }
                iconClassName={ QA.AMR_CONCEPT_HELP_ICON }
            />
        </td>
    </tr>;
