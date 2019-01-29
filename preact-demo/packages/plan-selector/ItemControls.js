import { h, Component }     from 'preact';

import Error                from '../ui-messages/Error';
import Loading              from '../ui-messages/Loading';
import * as planList        from '../plan-list/functions';
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
            /// removeError,
            removeLoading,
            /// renameError,
            renameLoading,
            openedPlanUid,
        },
    }) {
        const isAdding =    addLoading === openedPlanUid;
        const isRemoving =  removeLoading === openedPlanUid;
        const isRenaming =  renameLoading === openedPlanUid;

        return (
            <div className={ S.className }>{
                isAdding
                    ? addError
                        ? <Error className={ S.icon } justIcon message={ addError.toString() } />
                        : <Loading className={ S.icon } justIcon message="Saving." />
                : isRemoving
                    ? <Loading className={ S.icon } justIcon message="Removing." />
                    : [
                        ( isRenaming
                            ? <Loading className={ S.icon } justIcon message="Saving." />
                            : <button onClick={ this.onClickEdit }>📝</button>
                        ),
                        <button onClick={ this.onClickRemove }>🗑️</button>,
                    ]
            }</div>
        );
    }
});
