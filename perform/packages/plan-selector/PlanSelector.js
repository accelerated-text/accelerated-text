import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { Error, Loading }   from '../ui-messages/';
import OpenedPlanContext    from '../accelerated-text/OpenedPlanContext';
import PlanActions          from '../document-plans/Actions';
import planTemplate         from '../document-plans/plan-template';
import { QA }               from '../tests/constants';
import UnexpectedWarning    from '../ui-messages/UnexpectedWarning';

import ItemControls         from './ItemControls';
import List                 from './List';
import S                    from './PlanSelector.sass';


export default PlanActions(
    class PlanSelector extends Component {

        static contextType =    OpenedPlanContext;

        static propTypes = {
            onCreatePlan:       PropTypes.func.isRequired,
            onDeletePlan:       PropTypes.func.isRequired,
            onUpdatePlan:       PropTypes.func.isRequired,
            planStatus:         PropTypes.object.isRequired,
        };

        onClickNew = evt => {

            const openedPlan =  this.context.openedPlan || {};

            const name = window.prompt(         // eslint-disable-line no-alert
                'Add a new Document Plan:',
                planTemplate.name,
            );
            name && this.props.onCreatePlan({
                dataSampleId:   openedPlan.dataSampleId     || planTemplate.dataSampleId,
                dataSampleRow:  openedPlan.dataSampleRow    || planTemplate.dataSampleRow,
                name,
            });
        }

        onClickSaveAs = evt => {
            const name = window.prompt(         // eslint-disable-line no-alert
                'Enter name for the new plan:',
                this.context.openedPlan.name,
            );
            name && this.props.onCreatePlan({
                ...this.context.openedPlan,
                name,
            });
        }

        render({
            onDeletePlan,
            onUpdatePlan,
            planStatus,
        }, __, {
            documentPlans,
            documentPlansError,
            documentPlansLoading,
            openPlanUid,
            openedPlan,
            openedPlanError,
            openedPlanLoading,
        }) {
            const noPlans =     ! documentPlans || ! documentPlans.totalCount;

            return (
                <div className={ S.className }>{
                    openedPlanLoading
                        ? <Loading message="Loading plans." />
                    : openedPlanError
                        ? <Error message="Loading error! Please refresh the page." />
                    : noPlans
                        ? <button
                            children="➕ New document plan"
                            className={ QA.BTN_NEW_PLAN }
                            onClick={ this.onClickNew }
                        />
                    : ! openedPlan
                        ? <UnexpectedWarning />
                        : [
                            <List
                                onClickNew={ this.onClickNew }
                                onClickSaveAs={ this.onClickSaveAs }
                                onChangeSelected={ openPlanUid }
                                openedPlan={ openedPlan }
                                plans={ documentPlans.items }
                                selectedUid={ openedPlan && openedPlan.uid }
                            />,
                            <ItemControls
                                onDelete={ onDeletePlan }
                                onUpdate={ onUpdatePlan }
                                plan={ openedPlan }
                                status={ planStatus }
                            />,
                        ]
                }</div>
            );
        }
    }
);
