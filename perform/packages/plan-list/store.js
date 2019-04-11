import { prop }         from 'ramda';

import { createPlan }   from '../document-plans/functions';

import { sortPlans }    from './functions';


export default {

    getInitialState: () => ({
        addCheckError:          null,
        addedPlan:              null,
        createdUid:             null,
        getListError:           null,
        getListLoading:         false,
        openedPlanUid:          null,
        previousOpenedPlanUid:  null,
        uids:                   [],
    }),

    documentPlans: {

        onCreate: ( createdPlan, { getStoreState, state }) => {
            if( state.addedPlan === createdPlan ) {
                getStoreState( 'documentPlans' );
                return {
                    addedPlan:      null,
                    createdUid:     createdPlan.uid,
                    openedPlanUid:  createdPlan.uid,
                    uids:           [ createdPlan.uid, ...state.uids ],
                };
            }
        },
    },

    planList: {

        onSelectPlan: ( openedPlanUid, { state }) => ({
            openedPlanUid,
            previousOpenedPlanUid:    state.openedPlanUid,
        }),

        onAddNew: ( fields, { state }) => (
            fields.name
                ? { addedPlan:      createPlan( fields ) }
                : { addCheckError:  'Cannot create a document plan without the name.' }
        ),

        /// Get list -----------------------------------------------------------

        onGetList: ( _, { state }) => (

            state.getListLoading && {
                getListError:   'Will not start a new document plan list request while the previous one is not finished. Please wait.',
            }
        ),

        onGetListStart: () => ({
            getListLoading:     true,
        }),

        onGetListError: getListError => ({
            getListError,
            getListLoading:     false,
        }),

        onGetListResult: ( newPlans, { state }) => {

            const {
                createdUid,
                openedPlanUid,
            } =  state;

            const sortedUids =      sortPlans( newPlans ).map( prop( 'uid' ));
            const isCreatedSaved =  !createdUid || sortedUids.includes( createdUid );
            const uids = (
                isCreatedSaved
                    ? sortedUids
                    : [ createdUid, ...sortedUids ]
            );

            return {
                createdUid:             isCreatedSaved ? null : createdUid,
                getListError:           null,
                getListLoading:         false,
                openedPlanUid: (
                    uids.includes( openedPlanUid )
                        ? openedPlanUid
                        : ( uids[0] || null )
                ),
                previousOpenedPlanUid:  state.openedPlanUid,
                uids,
            };
        },
    },
};
