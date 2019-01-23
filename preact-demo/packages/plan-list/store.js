import {
    getActiveId,
    removeItem,
    sortPlans,
    updateItem,
} from './functions';


export default {

    getInitialState: () => ({
        addError:           null,
        addLoading:         false,
        getListError:       null,
        getListLoading:     false,
        plans:              [],
        removeError:        null,
        removeLoading:      false,
        removeResult:       null,
        renameError:        null,
        renameLoading:      false,
        selectedPlanId:     null,
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
            selectedPlanId:   newPlan.id,
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

            const plans =           sortPlans( newPlans );

            return {
                getListError:   null,
                getListLoading: false,
                plans,
                selectedPlanId: getActiveId( plans, state.selectedPlanId ),
            };
        },

        /// Remove -------------------------------------------------------------

        onRemoveStart: () => ({
            removeLoading:  true,
        }),

        onRemoveError: removeError => ({
            removeError,
            removeLoading:  false,
        }),

        onRemoveResult: ( removeResult, { state }) => ({
            plans:          removeItem( state.plans, removeResult ),
            removeError:    null,
            removeLoading:  false,
            removeResult,
        }),

        /// Rename -------------------------------------------------------------

        onRenameStart: () => ({
            renameLoading:  true,
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

        onSelectPlan: selectedPlanId => ({
            selectedPlanId,
        }),
    },
};
