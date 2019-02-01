import uuid             from 'uuid';

import emptyPlan        from '../document-plans/plan-template';
import {
    addMissingUids,
    getActiveUid,
    patchStatus,
    removeItem,
    sortPlans,
    updateItem,
} from './functions';


const CLEAN_STATUS = {
    addError:           null,
    addLoading:         false,
    renameError:        null,
    renameLoading:      false,
    removeError:        null,
    removeLoading:      false,
};


export default {

    getInitialState: () => ({
        addCheckError:  null,
        addedPlan:     null,
        getListError:   null,
        getListLoading: false,
        openedPlanUid:  null,
        plans:          null,
        removedPlan:    null,
        statuses:       {},
    }),

    planList: {

        /// Add ----------------------------------------------------------------

        onAddNew: ( name, { state }) => {

            if( !name ) {
                return {
                    addCheckError:  'Cannot create a document plan without the name.',
                };
            } else if( state.addedPlan && state.statuses[state.addedPlan.uid].addLoading ) {
                return {
                    addCheckError: 'Cannot create a new document plan while the previous one is not yet saved. Please wait.',
                };
            } else {
                const uid =         uuid.v4();
                const addedPlan = {
                    ...emptyPlan,
                    createdAt:      +new Date,
                    name,
                    uid,
                };

                return {
                    addedPlan,
                    openedPlanUid:  uid,
                    plans: [ addedPlan, ...state.plans ],
                    ...patchStatus( state, uid, CLEAN_STATUS ),
                };
            }
        },

        onAddStart: ( uid, { state }) =>
            patchStatus( state, uid, {
                addLoading: true,
            }),

        onAddResult: ( newPlan, { state }) => {

            let plans;
            try {
                plans =             updateItem( state.plans, newPlan );
                return {
                    addedPlan:      null,
                    plans,
                    ...patchStatus( state, newPlan.uid, {
                        addError:   null,
                        addLoading: false,
                    }),
                };
            } catch( addError ) {
                return patchStatus( state, newPlan.uid, {
                    addError,
                    addLoading:     false,
                });
            }
        },

        onAddError: ({ uid, addError }, { state }) =>
            patchStatus( state, uid, {
                addError,
                addLoading:     false,
            }),

        /// Get list -----------------------------------------------------------

        onGetList: ( _, { state }) =>
            state.getListLoading && {
                getListError:   'Will not start a new document plan list request while the previous one is not finished. Please wait.',
            },

        onGetListStart: () => ({
            getListLoading:     true,
        }),

        onGetListError: getListError => ({
            getListError,
            getListLoading:     false,
        }),

        onGetListResult: ( newPlans, { state }) => {

            const sortedPlans = sortPlans( addMissingUids( newPlans ));
            const plans = (
                state.addedPlan
                    ? [ state.addedPlan, ...sortedPlans ]
                    : sortedPlans
            );

            const statuses =    { ...state.statuses };
            for( let plan of plans ) {
                statuses[plan.uid] =    statuses[plan.uid] || CLEAN_STATUS;
            }

            return {
                getListError:   null,
                getListLoading: false,
                plans,
                openedPlanUid:  getActiveUid( plans, state.openedPlanUid ),
                statuses,
            };
        },

        /// Remove -------------------------------------------------------------

        onRemovePlan: ( item, { state }) => {
            const status =  state.statuses[item.uid];

            if( status.removeLoading ) {
                return patchStatus( state, item.uid, {
                    removeError:    'Cannot remove document plan while the previous request is not finished. Please wait.',
                });
            }
        },

        onRemoveStart: ( item, { state }) =>
            patchStatus( state, item.uid, {
                removeLoading:  true,
            }),

        onRemoveError: ({ removeError, item }, { state }) =>
            patchStatus( state, item.uid, {
                removeError,
                removeLoading:  false,
            }),

        onRemoveResult: ( removedPlan, { state }) => {
            const plans =           removeItem( state.plans, removedPlan );

            return {
                plans,
                openedPlanUid:      getActiveUid( plans, state.openedPlanUid ),
                removedPlan,
                ...patchStatus( state, removedPlan.uid, {
                    removeError:    null,
                    removeLoading:  false,
                }),
            };
        },

        /// Rename -------------------------------------------------------------

        onRenamePlan: ({ item, name }, { state }) => {

            if( !name ) {
                return patchStatus( state, item.uid, {
                    renameError:    'Please supply a non-empty name for the plan.',
                });
            }

            const status =  state.statuses[item.uid];

            if( status.renameLoading ) {
                return patchStatus( state, item.uid, {
                    renameError:    'Cannot rename the document plan while the previous request is not finished. Please wait.',
                });
            }

            return {
                plans: updateItem( state.plans, {
                    ...item,
                    name,
                }),
            };
        },

        onRenameStart: ( item, { state }) =>
            patchStatus( state, item.uid, {
                renameLoading:  true,
            }),

        onRenameError: ({ renameError, item }, { state }) =>
            patchStatus( state, item, {
                renameError,
                renameLoading:  false,
            }),

        onRenameResult: ( updatedPlan, { state }) => ({
            plans:          updateItem( state.plans, updatedPlan ),
            ...patchStatus( state, updatedPlan.uid, {
                renameError:    null,
                renameLoading:  false,
            }),
        }),

        /// Other --------------------------------------------------------------

        onSelectPlan: openedPlanUid => ({
            openedPlanUid,
        }),
    },
};
