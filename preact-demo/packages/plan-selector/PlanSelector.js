import { h, Component } from 'preact';

import ClockSpinner     from '../clock-spinner/ClockSpinner';
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
                openedPlanId,
            },
        } = this.props;
        const plan =        planList.findById( plans, openedPlanId );

        if( !plan ) {
            return;
        }

        const planName =    window.prompt( 'Rename Document Plan:', plan.name );
        return this.props.E.planList.onRenamePlan({
            id:             openedPlanId,
            name:           planName,
        });
    }

    onClickNew = evt => {
        const planName = window.prompt( 'Add a new Document Plan:', 'Untitled Plan' );
        planName && this.props.E.planList.onAddNew( planName );
    }

    onClickRemove = evt => (
        window.confirm( '⚠️ Are you sure you want to remove this plan?' )
        && this.props.E.planList.onRemovePlan( this.props.planList.openedPlanId )
    );

    renderActions({
        planList: {
            addLoading,
            getListLoading,
            plans,
            removeLoading,
            renameLoading,
            openedPlanId,
        },
    }) {

        const hasPlans =    plans && plans.length;
        const noPlans =     !hasPlans;
        const isWeird = (
            noPlans && openedPlanId
            || hasPlans && !openedPlanId
        );
        const isLoading = (
            noPlans && getListLoading
            || noPlans && addLoading
            || openedPlanId === removeLoading
            || openedPlanId === renameLoading
        );

        return (
            isWeird
                ? <span>❓</span>
            : isLoading
                ? <ClockSpinner />
            : openedPlanId
                ? [
                    <button onClick={ this.onClickEdit }>📝</button>,
                    <button onClick={ this.onClickRemove }>❌</button>,
                ]
            : <span>❓</span>
        );
    }


    render({
        planList: {
            getListLoading,
            getListError,
            plans,
            removeLoading,
            renameLoading,
            openedPlanId,
        }}) {
        return (
            <div className={ S.className }>
                <div className={ S.main }>{
                    !plans
                        ? (
                            <span>{
                                getListLoading
                                    ? 'Loading plans...'
                                : getListError
                                    ? 'Loading error!'
                                    : '❓'
                            }</span>
                        )
                    : plans.length
                        ? (
                            <select
                                onChange={ this.onChange }
                                value={ openedPlanId }
                            >
                                <option value={ ADD_NEW }>➕ New...</option>
                                <optgroup label="📂 Open a plan">
                                    { plans.map( plan =>
                                        <option value={ plan.id }>
                                            📄 { plan.name }
                                        </option>
                                    )}
                                </optgroup>
                            </select>
                        )
                        : (
                            <button onClick={ this.onClickNew }>
                                ➕ New document plan
                            </button>
                        )
                }</div>
                <div className={ S.actions }>
                    { this.renderActions( this.props ) }
                </div>
            </div>
        );
    }
});
