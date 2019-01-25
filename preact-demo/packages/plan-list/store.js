import uuid             from 'uuid';

import emptyPlan        from './empty-plan';
import {
    addMissingUids,
    getActiveUid,
    removeItem,
    sortPlans,
    updateItem,
} from './functions';


export default {

    getInitialState: () => ({
        addError:       null,
        addNewUid:      null,
        addLoading:     null,
        getListError:   null,
        getListLoading: false,
        openedPlanUid:  null,
        plans:          null,
        removeError:    null,
        removeLoading:  null,
        removeResult:   null,
        renameError:    null,
        renameLoading:  null,
    }),

    planList: {

        /// Add ----------------------------------------------------------------

        onAddNew: ( name, { state }) => {

            if( !name ) {
                return {
                    addError:   'Cannot create a document plan without the name.',
                };
            } else if( state.addLoading ) {
                return {
                    addError:   'Cannot create a new document plan while the previous one is not yet saved.',
                };
            } else {
                const uid =         uuid.v4();
                const newPlan = {
                    ...emptyPlan,
                    createdAt:      +new Date,
                    name,
                    uid,
                };

                return {
                    addNewPlan:     newPlan,
                    openedPlanUid:  uid,
                    plans: [
                        newPlan,
                        ...state.plans,
                    ],
                };
            }
        },

        onAddStart: uid => ({
            addLoading:     uid || true,
        }),

        onAddResult: ( newPlan, { state }) => {

            let plans;
            try {
                plans =     updateItem( state.plans, newPlan );
                return {
                    addError:       null,
                    addLoading:     false,
                    openedPlanUid:
                        state.openedPlanUid === state.addLoading
                            ? newPlan.uid
                            : state.openedPlanUid,
                    plans,
                };
            } catch( addError ) {
                return {
                    addError,
                    addLoading:     false,
                };
            }
        },

        onAddError: addError => ({
            addError,
            addLoading:     false,
        }),

        /// Get list -----------------------------------------------------------

        onGetListStart: ( _,  { state }) => (
            state.getListLoading
                ? { getListError:   'Will not start a new document plan list request while the previous one is not finished.' }
                : { getListLoading: true }
        ),

        onGetListError: getListError => ({
            getListError,
            getListLoading:     false,
        }),

        onGetListResult: ( newPlans, { state }) => {

            const sortedPlans = sortPlans( addMissingUids( newPlans ));
            const plans = (
                state.addNewPlan
                    ? [ state.addNewPlan, ...sortedPlans ]
                    : sortedPlans
            );

            return {
                getListError:   null,
                getListLoading: false,
                plans,
                openedPlanUid:  getActiveUid( plans, state.openedPlanUid ),
            };
        },

        /// Remove -------------------------------------------------------------

        onRemovePlan: ( item, { state }) => {
            if( !item ) {
                return {
                    removeError:    'Missing item for document plan remove operation.',
                };
            } else if( state.removeLoading ) {
                return {
                    removeError:    'Cannot remove document plan while the previous request is not finished.',
                };
            } else {
                const plans =       removeItem( state.plans, item );
                return {
                    plans,
                    openedPlanUid:  getActiveUid( plans, state.openedPlanUid ),
                };
            }
        },

        onRemoveStart: uid => ({
            removeLoading:  uid || true,
        }),

        onRemoveError: removeError => ({
            removeError,
            removeLoading:  false,
        }),

        onRemoveResult: removeResult => ({
            removeError:    null,
            removeLoading:  false,
            removeResult,
        }),

        /// Rename -------------------------------------------------------------

        onRenamePlan: ({ item, name }, { state }) => (
            !item
                ? { renameError:    'Missing item for document plan rename operation.' }
            : !name
                ? { renameError:    'Missing UID for document plan rename operation.' }
            : state.renameLoading
                ? { renameError:    'Cannot rename the document plan while the previous request is not finished.' }
                : {
                    plans: updateItem( state.plans, {
                        ...item,
                        name,
                    }),
                }
        ),

        onRenameStart: uid => ({
            renameLoading:  uid || true,
        }),

        onRenameError: renameError => ({
            renameError,
            renameLoading:  false,
        }),

        onRenameResult: ( updatedPlan, { state }) => ({
            plans:          updateItem( state.plans, updatedPlan ),
            renameError:    null,
            renameLoading:  false,
        }),

        /// Other --------------------------------------------------------------

        onSelectPlan: openedPlanUid => ({
            openedPlanUid,
        }),
    },
};
