import emptyPlan        from './empty-plan';
import {
    findById,
    getActiveId,
    removeById,
    sortPlans,
    updateByTmpId,
    updateItem,
} from './functions';


export default {

    getInitialState: () => ({
        addError:           null,
        addLoading:         false,
        addTmpId:           null,
        getListError:       null,
        getListLoading:     false,
        openedPlanId:       null,
        plans:              null,
        removeError:        null,
        removeLoading:      false,
        removeResult:       null,
        renameError:        null,
        renameLoading:      false,
    }),

    planList: {

        /// Add ----------------------------------------------------------------

        onAddNew: ( name, { state }) => {

            if( !name || state.addLoading ) {
                return;
            }

            const tmpId =       Math.random().toString();

            const tmpPlan = {
                ...emptyPlan,
                createdAt:      +new Date,
                name,
                tmpId,
            };

            return {
                addTmpId:       tmpId,
                openedPlanId:   tmpId,
                plans: [
                    tmpPlan,
                    ...state.plans,
                ],
            };
        },

        onAddStart: () => ({
            addLoading:     true,
        }),

        onAddResult: ( newPlan, { state }) => ({
            addError:       null,
            addLoading:     false,
            addTmpId:       null,
            plans:          updateByTmpId( state.plans, state.addTmpId, newPlan ),
            openedPlanId:
                state.openedPlanId === state.addTmpId
                    ? newPlan.id
                    : state.openedPlanId,
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
            if( id && !state.removeLoading ) {
                const plans =       removeById( state.plans, id );
                return {
                    plans,
                    openedPlanId:   getActiveId( plans, state.openedPlanId ),
                };
            }
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

        onRenamePlan: ({ id, name }, { state }) =>
            ( id && name && !state.renameLoading )
                ? {
                    plans:
                        updateItem( state.plans, {
                            ...findById( state.plans, id ),
                            name,
                        }),
                }
                : null,

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
