import { h, Component }     from 'preact';

import Error                from '../ui-messages/Error';
import Loading              from '../ui-messages/Loading';
import OpenedPlanContext    from '../accelerated-text/OpenedPlanContext';
import planTemplate         from '../document-plans/plan-template';
import { QA }               from '../tests/constants';
import UnexpectedWarning    from '../ui-messages/UnexpectedWarning';

import ItemControls         from './ItemControls';
import List                 from './List';
import S                    from './PlanSelector.sass';


export default class PlanSelector extends Component {

    static contextType =    OpenedPlanContext;

    onClickNew = evt => {

        const openedPlan =  this.context.openedPlan || {};

        const name = window.prompt(         // eslint-disable-line no-alert
            'Add a new Document Plan:',
            planTemplate.name,
        );
        name && this.context.E.planList.onAddNew({
            contextId:      openedPlan.contextId        || planTemplate.contextId,
            dataSampleId:   openedPlan.dataSampleId     || planTemplate.dataSampleId,
            dataSampleRow:  openedPlan.dataSampleRow    || planTemplate.dataSampleRow,
            name,
        });
    }

    onClickSaveAs = evt => {
        const {
            E,
            openedPlan,
        } = this.context;

        const name = window.prompt(         // eslint-disable-line no-alert
            'Enter name for the new plan:',
            openedPlan.name,
        );
        name && E.planList.onAddNew({
            ...openedPlan,
            name,
        });
    }

    render( _, __, {
        E,
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
                            plans={ documentPlans.items }
                            selectedUid={ openedPlan && openedPlan.uid }
                        />,
                        <ItemControls
                            onDelete={ E.documentPlans.onDelete }
                            onUpdate={ E.documentPlans.onUpdate }
                            plan={ openedPlan }
                            status={{}}
                        />,
                    ]
            }</div>
        );
    }
}
