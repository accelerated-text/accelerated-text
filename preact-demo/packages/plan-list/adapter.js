import debugSan         from '../debug-san/';
import { fixPlan }      from '../document-plans/functions';
import { GET }          from '../document-plans/api';


const debug =           debugSan( 'plan-list/adapter' );


export default {

    componentDidMount: ( _, { E }) => {

        E.planList.onGetList();
    },

    documentPlans: {

        onCreateResult: ( _, { E }) =>
            E.planList.onGetList.async(),

        onDeleteResult: ( _, { E }) =>
            E.planList.onGetList.async(),
    },

    planList: {

        onAddNew: ( _, { E, getStoreState }) =>

            E.documentPlans.onCreate.async(
                getStoreState( 'planList' ).addedPlan,
            ),

        onGetList: ( _, { E, getStoreState }) =>

            !getStoreState( 'planList' ).getListLoading &&
                E.planList.onGetListStart.async(),

        onGetListStart: ( _, { E }) =>

            GET( '/' )
                .then( debug.tapThen( 'onGetListStart' ))
                .then( result => {
                    const plans =   result.map( fixPlan );
                    debug( 'onGetListStart plans', plans );
                    E.documentPlans.onGetAList( plans );
                    E.planList.onGetListResult( plans );
                })
                .catch( debug.tapCatch( 'onGetListStart' ))
                .catch( E.planList.onGetListError ),
    },
};
