import pTap             from 'p-tap';

import { fixPlan }      from '../document-plans/functions';
import { GET }          from '../document-plans/api';


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
                .then( result => {
                    const plans =   result.map( fixPlan );
                    E.documentPlans.onGetAList( plans );
                    E.planList.onGetListResult( plans );
                })
                .catch( pTap( console.error ))
                .catch( E.planList.onGetListError ),
    },
};
