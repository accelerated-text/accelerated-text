import { h, Component }     from 'preact';
import { path }             from 'ramda';

import { composeQueries }   from '../graphql/';
import { rglPlans }    from '../graphql/queries.graphql';
import { getPlanByUid }     from '../document-plans/functions';

import Context              from './OpenedPlanContext';
import { OPENED_PLAN_UID }  from './constants';


export default composeQueries({
    rglPlans,
})( class OpenedPlanContextProvider extends Component {

    static getDerivedStateFromProps(
        { rglPlans: { documentPlans, error, loading }},
        { plan },
    ) {
        const skip = (
            loading
            || error
            || ! documentPlans
            || (
                plan
                && plan === getPlanByUid( documentPlans, plan.uid )
            )
        );
        if( ! skip ) {
            const newOpenedPlan = (
                getPlanByUid( documentPlans, plan && plan.uid )
                || ( plan && ! plan.id && plan )
                || getPlanByUid( documentPlans, localStorage.getItem( OPENED_PLAN_UID ))
                || path([ 'items', 0 ], documentPlans )
            );
            if( newOpenedPlan ) {
                localStorage.setItem( OPENED_PLAN_UID, newOpenedPlan.uid );
            }
            return { plan: newOpenedPlan };
        }
    }

    value =                 {};

    state = {
        plan:               null,
    };

    openPlan = plan => {
        this.setState({ plan });
        localStorage.setItem(
            OPENED_PLAN_UID,
            plan && plan.uid || null,
        );
    };

    openPlanUid = uid => {
        const plan =        getPlanByUid( this.props.rglPlans.documentPlans, uid );
        if( plan ) {
            this.openPlan( plan );
        } else {
            throw Error( `Tried to select a non-existent document plan ${ uid }.` );
        }
    }

    render({ children, documentPlans }, { plan }) {
        return <Context.Provider
            children={ children }
            value={ Object.assign( this.value, {
                error:          ! plan && rglPlans.error,
                loading:        ! plan && rglPlans.loading,
                openPlan:       this.openPlan,
                openPlanUid:    this.openPlanUid,
                plan,
            }) }
        />;
    }
});
