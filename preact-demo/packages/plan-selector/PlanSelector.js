import { h, Component } from 'preact';

import { useStores }    from '../vesa/';
import * as planList    from '../plan-list/functions';

import S                from './PlanSelector.sass';


const ADD_NEW =         `ADD-NEW-${ Math.random() }`;


export default useStores([
    'planList',
])( class PlanSelector extends Component {

    onChange = evt =>
        ( evt.target.value === ADD_NEW )
            ? this.onClickNew()
            : this.props.E.planList.onSelectPlan( evt.target.value )

    onClickEdit = evt => {
        const {
            planList: {
                plans,
                selectedPlanId,
            },
        } = this.props;
        const plan =        planList.findById( plans, selectedPlanId );

        if( !plan ) {
            return;
        }

        const planName =    window.prompt( 'Rename Document Plan:', plan.name );
        return this.props.E.planList.onRenamePlan({
            id:             selectedPlanId,
            name:           planName,
        });
    }

    onClickNew = evt => {
        const planName = window.prompt( 'Add a new Document Plan:', 'Untitled Plan' );
        planName && this.props.E.planList.onAddNew( planName );
    }

    onClickRemove = evt => (
        window.confirm( '⚠️ Are you sure you want to remove this plan?' )
        && this.props.E.planList.onRemovePlan( this.props.planList.selectedPlanId )
    );

    render({
        planList: {
            getListError,
            getListLoading,
            addError,
            addLoading,
            plans,
            selectedPlanId,
        }}) {
        return (
            <div className={ S.className }>
                { plans.length
                    ? [
                        <select
                            onChange={ this.onChange }
                            value={ selectedPlanId }
                        >
                            <optgroup label="📂 Open a plan">
                                { plans.map( plan => (
                                    <option value={ plan.id }>
                                        📄 { plan.name }
                                    </option>
                                ))}
                            </optgroup>
                            <option value={ ADD_NEW }>➕ New...</option>
                        </select>,
                        ...( selectedPlanId
                            ? [
                                <button onClick={ this.onClickEdit }>📝</button>,
                                <button onClick={ this.onClickRemove }>❌</button>,
                            ]
                            : []
                        ),
                    ]
                : getListLoading
                    ? <span>Loading list...</span>
                : addLoading
                    ? <span>Creating the plan...</span>
                : (
                    <button onClick={ this.onClickNew }>
                        ➕ New document plan
                    </button>
                )}
            </div>
        );
    }
});
