import { h, Component }     from 'preact';
import { path }             from 'ramda';

import { composeQueries }   from '../graphql/';
import { documentPlans }    from '../graphql/queries.graphql';

import Context              from './OpenedPlanContext';


const findByUid = ( documentPlans, uid ) => (
    uid
    && documentPlans
    && documentPlans.items
    && documentPlans.items.find( plan =>
        plan.uid === uid
    )
);


export const OPENED_PLAN_UID =  'accelerated-text/OpenedPlanContext#openedPlanUid';


export default composeQueries({
    documentPlans,
})( class OpenedPlanContextProvider extends Component {

    static getDerivedStateFromProps(
        { documentPlans: { documentPlans, error, loading }},
        { openedPlan },
    ) {
        const skip = (
            loading
            || error
            || ! documentPlans
            || (
                openedPlan
                && openedPlan === findByUid( documentPlans, openedPlan.uid )
            )
        );
        if( ! skip ) {
            return {
                openedPlan: (
                    findByUid( documentPlans, openedPlan && openedPlan.uid )
                    || ( openedPlan && ! openedPlan.id && openedPlan )
                    || findByUid( documentPlans, localStorage.getItem( OPENED_PLAN_UID ))
                    || path([ 'items', 0 ], documentPlans )
                ),
            };
        }
    }

    value =                 {};

    state = {
        openedPlan:         null,
    };

    openPlan = openedPlan => {
        this.setState({ openedPlan });
        localStorage.setItem(
            OPENED_PLAN_UID,
            openedPlan && openedPlan.uid || null,
        );
    };

    openPlanUid = uid => {
        const openedPlan =  findByUid( this.props.documentPlans.documentPlans, uid );
        if( openedPlan ) {
            this.openPlan( openedPlan );
        } else {
            throw Error( `Tried to select a non-existent document plan ${ uid }.` );
        }
    }

    render({
        children,
        documentPlans: {
            documentPlans,
            error:          documentPlansError,
            loading:        documentPlansLoading,
        },
    }, {
        openedPlan,
    }) {
        return <Context.Provider
            children={ children }
            value={ Object.assign( this.value, {
                documentPlans,
                documentPlansError,
                documentPlansLoading,
                openPlan:               this.openPlan,
                openPlanUid:            this.openPlanUid,
                openedPlan,
                openedPlanError:        ! openedPlan && documentPlansError,
                openedPlanLoading:      ! openedPlan && documentPlansLoading,
            }) }
        />;
    }
});
