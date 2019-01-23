const sortByCreatedAt = ( a, b ) => (
    ( a.createdAt && b.createdAt )
        ? ( b.createdAt - a.createdAt )
    : a.createdAt
        ? -1
    : b.createdAt
        ? 1
    : -1
);


export default {

    getInitialState: () => ({
        addError:       null,
        addLoading:     false,
        getListError:   null,
        getListLoading: false,
        plans:          [],
        removeError:    null,
        removeLoading:  false,
        removeResult:   null,
        renameError:    null,
        renameLoading:  false,
        selectedPlan:   null,
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
            selectedPlan:   newPlan.id,
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

        onGetListResult: ( plans, { state }) => ({
            getListError:   null,
            getListLoading: false,
            plans:          plans.sort( sortByCreatedAt ),
            selectedPlan: (
                state.selectedPlan
                    ? state.selectedPlan
                    : plans[0] && plans[0].id || null
            ),
        }),

        /// Remove -------------------------------------------------------------

        onRemoveStart: () => ({
            removeLoading:  true,
        }),

        onRemoveError: removeError => ({
            removeError,
            removeLoading:  false,
        }),

        onRemoveResult: ( removeResult, { state }) => {

            const idx = state.plans.findIndex(
                plan => plan.id === removeResult.id
            );

            if( !removeResult.id || idx === -1 ) {
                return {
                    removeError:    null,
                    removeLoading:  false,
                    removeResult,
                };
            }

            /// [...].splice() modifies the array in-place:
            const plans =   [ ...state.plans ];
            plans.splice( idx, 1 );

            return {
                plans,
                removeError:    null,
                removeLoading:  false,
                removeResult,
            };
        },

        /// Rename -------------------------------------------------------------

        onRenameStart: () => ({
            renameLoading:  true,
        }),

        onRenameError: renameError => ({
            renameError,
            renameLoading:  false,
        }),

        onRenameResult: ( newPlan, { state }) => {

            const idx = state.plans.findIndex(
                plan => plan.id === newPlan.id
            );

            if( !newPlan.id || idx === -1 ) {
                return {
                    renameError:    null,
                    renameLoading:  false,
                };
            }

            /// [...].splice() modifies the array in-place:
            const plans =   [ ...state.plans ];
            plans.splice( idx, 1, newPlan );

            return {
                plans,
                renameError:    null,
                renameLoading:  false,
            };
        },

        /// Other --------------------------------------------------------------

        onSelectPlan: selectedPlan => ({
            selectedPlan,
        }),
    },
};
