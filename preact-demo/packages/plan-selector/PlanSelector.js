import { h, Component }     from 'preact';

import ClockSpinner         from '../clock-spinner/ClockSpinner';
import Error                from '../ui-messages/Error';
import Info                 from '../ui-messages/Info';
import * as planList        from '../plan-list/functions';
import UnexpectedWarning    from '../ui-messages/UnexpectedWarning';
import { useStores }        from '../vesa/';

import S                    from './PlanSelector.sass';


const ADD_NEW =             `ADD-NEW-${ Math.random() }`;
const DEFAULT_NAME =        'Untitled Plan';


export default useStores([
    'planList',
])( class PlanSelector extends Component {

    onChangeSelect = evt =>
        ( evt.target.value === ADD_NEW )
            ? this.onClickNew()
            : this.props.E.planList.onSelectPlan( evt.target.value )

    onClickEdit = evt => {
        const {
            E,
            planList: {
                plans,
                openedPlanUid,
            },
        } = this.props;

        const item =    planList.findByUid( plans, openedPlanUid );
        if( !item ) {
            return E.planList.onGetList();
        }

        const name =    window.prompt( 'Rename Document Plan:', item.name );
        if( !name ) {
            return;
        }

        return E.planList.onRenamePlan({ item, name });
    }

    onClickNew = evt => {
        const planName = window.prompt( 'Add a new Document Plan:', DEFAULT_NAME );
        planName && this.props.E.planList.onAddNew( planName );
    }

    onClickRemove = () => (
        window.confirm( '⚠️ Are you sure you want to remove this plan?' )
            && this.props.E.planList.onRemovePlan(
                planList.findByUid(
                    this.props.planList.plans,
                    this.props.planList.openedPlanUid
                )
            )
    )

    renderActions({
        planList: {
            addError,
            addLoading,
            getListError,
            getListLoading,
            plans,
            removeError,
            removeLoading,
            renameError,
            renameLoading,
            openedPlanUid,
        },
    }) {
        const hasPlans =    plans && plans.length;
        const noPlans =     !hasPlans;

        const isUnexpected = (
            noPlans && openedPlanUid
            || hasPlans && !openedPlanUid
        );

        const isLoading = (
            noPlans && getListLoading
            || noPlans && addLoading
            || openedPlanUid === addLoading
            || openedPlanUid === removeLoading
            || openedPlanUid === renameLoading
        );

        const errorList =
            [ getListError, addError, removeError, renameError ]
                .filter( x => x );
        const isError = (
            errorList.length
            && ( noPlans || isLoading )
        );
        const renderError = err =>
            err
                ? <Error message={ err.toString() } />
                : null;

        return (
            isUnexpected
                ? <UnexpectedWarning justIcon />
            : isLoading
                ? [
                    <ClockSpinner className={ S.spinner } />,
                    ...( isError ? errorList.map( renderError ) : []),
                ]
            : isError
                ? errorList.map( renderError )
            : openedPlanUid
                ? [
                    <button onClick={ this.onClickEdit }>📝</button>,
                    <button onClick={ this.onClickRemove }>🗑️</button>,
                ]
            : <UnexpectedWarning justIcon />
        );
    }


    render({
        planList: {
            getListLoading,
            getListError,
            plans,
            removeLoading,
            renameLoading,
            openedPlanUid,
        }}) {
        return (
            <div className={ S.className }>
                <div className={ S.main }>{
                    !plans
                        ? (
                            getListLoading
                                ? <Info message="Loading plans." />
                            : getListError
                                ? <Error message="Loading error! Please refresh the page." />
                                : <UnexpectedWarning />
                        )
                    : plans.length
                        ? (
                            <select
                                onChange={ this.onChangeSelect }
                                value={ openedPlanUid }
                            >
                                <option value={ ADD_NEW }>➕ New...</option>
                                <optgroup label="📂 Open a plan">
                                    { plans.map( plan =>
                                        <option value={ plan.uid }>
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
