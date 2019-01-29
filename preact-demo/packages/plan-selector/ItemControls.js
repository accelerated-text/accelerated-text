import { h, Component }     from 'preact';

import ClockSpinner         from '../clock-spinner/ClockSpinner';
import Error                from '../ui-messages/Error';
import * as planList        from '../plan-list/functions';
import UnexpectedWarning    from '../ui-messages/UnexpectedWarning';
import { useStores }        from '../vesa/';

import S                    from './ItemControls.sass';


export default useStores([
    'planList',
])( class PlanSelectorItemControls extends Component {

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

    onClickRemove = () => (
        window.confirm( '⚠️ Are you sure you want to remove this plan?' )
            && this.props.E.planList.onRemovePlan(
                planList.findByUid(
                    this.props.planList.plans,
                    this.props.planList.openedPlanUid
                )
            )
    )

    render({
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
        console.log( 'ItemControls#render' );
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
                ? <Error className={ S.icon } message={ err.toString() } />
                : null;

        console.log({
            hasPlans,
            noPlans,
            isUnexpected,
            isLoading,
            errorList,
            isError,
            openedPlanUid,
        });

        return (
            <div className={ S.className }>{
                isUnexpected
                    ? <UnexpectedWarning className={ S.icon } justIcon />
                : isLoading
                    ? [
                        <ClockSpinner className={ S.icon } />,
                        ...( isError ? errorList.map( renderError ) : []),
                    ]
                : isError
                    ? errorList.map( renderError )
                : openedPlanUid
                    ? [
                        <button onClick={ this.onClickEdit }>📝</button>,
                        <button onClick={ this.onClickRemove }>🗑️</button>,
                    ]
                : <UnexpectedWarning className={ S.icon } justIcon />
            }</div>
        );
    }
});
