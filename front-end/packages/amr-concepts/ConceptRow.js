import classnames               from 'classnames';
import { h }                    from 'preact';

import AmrBlock                 from '../nlg-blocks/AMR';
import ExpandableOverflow       from '../expandable-overflow/ExpandableOverflow';
import { QA }                   from '../tests/constants';

import DragInBlock              from '../drag-in-blocks/DragInBlock';
import S                        from './ConceptRow.sass';


export default ({ concept }) =>
    <tr className={ classnames( S.className, QA.AMR_CONCEPT ) }>
        <td className={ S.dragInBlock }>
            { concept &&
                <DragInBlock
                    className={ QA.AMR_CONCEPT_DRAG_BLOCK }
                    block={ AmrBlock }
                    mutation={{
                        concept_id:     concept.id,
                        concept_label:  concept.label,
                        concept_kind:   concept.kind,
                        roles:          JSON.stringify( concept.roles ),
                    }}
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
