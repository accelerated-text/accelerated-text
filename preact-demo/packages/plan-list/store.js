import {
    getActiveId,
    removeById,
    sortPlans,
    updateItem,
} from './functions';


export default {

    getInitialState: () => ({
        addError:           null,
        addLoading:         false,
        getListError:       null,
        getListLoading:     false,
        plans:              null,
        removeError:        null,
        removeLoading:      false,
        removeResult:       null,
        renameError:        null,
        renameLoading:      false,
        openedPlanId:       null,
    }),

    planList: {

        /// Add ----------------------------------------------------------------

        onAddStart: () => ({
            addLoading:     true,
        }),

        onAddResult: ( newPlan, { state }) => ({
            addError:       null,
            addLoading:     false,
            plans: [
                newPlan,
                ...state.plans,
            ],
            openedPlanId:   newPlan.id,
        }),

        onAddError: addError => ({
            addError,
            addLoading:     false,
        }),

        /// Get list -----------------------------------------------------------

        onGetListStart: () => ({
            getListLoading: true,
        }),

        onGetListError: getListError => ({
            getListError,
            getListLoading: false,
        }),

        onGetListResult: ( newPlans, { state }) => {

            const plans =       sortPlans( newPlans );

            return {
                getListError:   null,
                getListLoading: false,
                plans,
                openedPlanId:   getActiveId( plans, state.openedPlanId ),
            };
        },

        /// Remove -------------------------------------------------------------

        onRemovePlan: ( id, { state }) => {

            if( !id || state.removeLoading ) {
                return;
            }

            const plans =       removeById( state.plans, id );
            return {
                plans,
                openedPlanId:   getActiveId( plans, state.openedPlanId ),
            };
        },

        onRemoveStart: id => ({
            removeLoading:  id || true,
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

        onRenameStart: id => ({
            renameLoading:  id || true,
        }),

        onRenameError: renameError => ({
            renameError,
            renameLoading:  false,
        }),

        onRenameResult: ( newPlan, { state }) => ({
            plans:          updateItem( state.plans, newPlan ),
            renameError:    null,
            renameLoading:  false,
        }),

        /// Other --------------------------------------------------------------

        onSelectPlan: openedPlanId => ({
            openedPlanId,
        }),
    },
};
