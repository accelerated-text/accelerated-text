import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import composeContexts      from '../compose-contexts/';
import DocumentPlansContext from '../document-plans/Context';
import { Error, Loading }   from '../ui-messages/';
import OpenedPlanContext    from '../rgl/OpenedPlanContext';
import PlanActions          from '../document-plans/Actions';
import planTemplate         from '../document-plans/rgl-plan-template';
import { QA }               from '../tests/constants';
import UnexpectedWarning    from '../ui-messages/UnexpectedWarning';

import ItemControls         from './ItemControls';
import List                 from './List';
import S                    from './PlanSelector.sass';


export default PlanActions( composeContexts({
    documentPlans:          DocumentPlansContext,
    openedPlan:             OpenedPlanContext,
})( class PlanSelector extends Component {

    static propTypes = {
        documentPlans:      PropTypes.object.isRequired,
        onCreatePlan:       PropTypes.func.isRequired,
        onDeletePlan:       PropTypes.func.isRequired,
        onUpdatePlan:       PropTypes.func.isRequired,
        openedPlan:         PropTypes.object.isRequired,
        planStatus:         PropTypes.object.isRequired,
    };

    onClickNew = evt => {
        const {
            onCreatePlan,
            openedPlan: {
                plan =      {},
            },
        } = this.props;

        const name = window.prompt(         // eslint-disable-line no-alert
            'Add a new Abstract Meaning Representation:',
            planTemplate.name,
        );
        name && onCreatePlan({
            dataSampleId:   plan.dataSampleId   || planTemplate.dataSampleId,
            dataSampleRow:  plan.dataSampleRow  || planTemplate.dataSampleRow,
            name,
        });
    }

    onClickSaveAs = evt => {
        const {
            onCreatePlan,
            openedPlan: { plan },
        } = this.props;

        const name = window.prompt(         // eslint-disable-line no-alert
            'Enter name for the new AMR:',
            plan.name,
        );
        name && onCreatePlan({ ...plan, name });
    }

    render({
        documentPlans,
        onDeletePlan,
        onUpdatePlan,
        openedPlan,
        planStatus,
    }) {
        const noPlans =     ! documentPlans.plans || ! documentPlans.plans.totalCount;

        return (
            <div className={ S.className }>{
                openedPlan.loading
                    ? <Loading message="Loading AMRs." />
                : openedPlan.error
                    ? <Error message="Loading error! Please refresh the page." />
                : noPlans
                    ? <button
                        children="➕ New operation"
                        className={ QA.BTN_NEW_PLAN }
                        onClick={ this.onClickNew }
                    />
                : ! openedPlan.plan
                    ? <UnexpectedWarning />
                    : [
                        <List
                            onClickNew={ this.onClickNew }
                            onClickSaveAs={ this.onClickSaveAs }
                            onChangeSelected={ openedPlan.openPlanUid }
                            selectedPlan={ openedPlan.plan }
                            plans={ documentPlans.plans.items }
                        />,
                        <ItemControls
                            onDelete={ onDeletePlan }
                            onUpdate={ onUpdatePlan }
                            plan={ openedPlan.plan }
                            status={ planStatus }
                        />,
                    ]
            }</div>
        );
    }
}));
