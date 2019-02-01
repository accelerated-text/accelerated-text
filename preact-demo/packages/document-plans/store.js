import {
    createPlan,
    getStatus,
}   from './functions';
import statusTemplate   from './status-template';


const patchPlan = ( state, plan ) => ({
    plans: {
        ...state.plans,
        [plan.uid]: {
            ...state.plans[plan.uid],
            ...plan,
            updateCount:        1 + (
                state.plans[plan.uid]
                    ? state.plans[plan.uid].updateCount
                    : 0
            ),
        },
    },
});

const patchStatus = ( state, plan, patch ) => ({
    statuses: {
        ...state.statuses,
        [plan.uid]: {
            ...state.statuses[plan.uid],
            ...patch,
        },
    },
});

export default {

    getInitialState: () => ({
        plans:          {},
        statuses:       {},
        createdPlan:    null,
    }),

    documentPlans: {
        /// Create -----------------------------------------------------------------

        onCreate: ( someFields, { state }) => {
            if( !someFields || !someFields.name ) {
                return;
            }

            const createdPlan = createPlan( someFields );

            return {
                ...patchPlan( state, createdPlan ),
                ...patchStatus( state, createdPlan, statusTemplate ),
                createdPlan,
            };
        },

        onCreateStart: ( plan, { state }) =>
            patchStatus( state, plan, {
                createLoading:  true,
            }),

        onCreateError: ({ plan, createError }, { state }) =>
            patchStatus( state, plan, {
                createError,
                createLoading:  false,
            }),

        onCreateResult: ( plan, { state }) => ({
            ...patchPlan( state, plan ),
            ...patchStatus( state, plan, {
                createError:    null,
                createLoading:  false,
            }),
        }),

        /// Delete -----------------------------------------------------------------

        onDelete: ( plan, { state }) => {

            const {
                createLoading,
                deleteLoading,
                deletePending,
            } = getStatus( state, plan );

            if( !deleteLoading && !deletePending ) {
                return {
                    ...patchStatus( state, plan, {
                        deletePending:  !!createLoading,
                        updatePending:  false,
                        isDeleted:      true,
                    }),
                };
            }
        },

        onDeleteStart: ( plan, { state }) =>
            patchStatus( state, plan, {
                deleteLoading:  true,
                deletePending:  false,
            }),

        onDeleteError: ({ plan, deleteError }, { state }) =>
            patchStatus( state, plan, {
                deleteError,
                deleteLoading:  false,
            }),

        onDeleteResult: ( plan, { state }) => ({
            ...patchPlan( state, plan ),
            ...patchStatus( state, plan, {
                deleteError:    null,
                deleteLoading:  false,
            }),
        }),

        /// Read -------------------------------------------------------------------

        onRead: () => null,

        onReadStart: ( plan, { state }) =>
            patchStatus( state, plan, {
                readLoading:    true,
            }),

        onReadError: ({ plan, readError }, { state }) =>
            patchStatus( state, plan, {
                readError,
                readLoading:    false,
            }),

        onReadResult: ( plan, { state }) => ({
            ...patchPlan( state, plan ),
            ...patchStatus( state, plan, {
                readError:      null,
                readLoading:    false,
            }),
        }),

        /// Update -----------------------------------------------------------------

        onUpdate: ( plan, { state }) => {

            const {
                isDeleted,
                createLoading,
                deleteLoading,
                deletePending,
                updateLoading,
                updatePending,
            } = getStatus( state, plan );

            const shouldSkip = (
                isDeleted
                || deleteLoading
                || deletePending
            );

            const shouldWait = (
                !plan.id
                || createLoading
                || updateLoading
                || updatePending
            );

            if( !shouldSkip ) {
                return {
                    ...patchPlan( state, plan ),
                    ...patchStatus( state, plan, {
                        updatePending:  !!shouldWait,
                    }),
                };
            }
        },

        onUpdateStart: ( plan, { state }) =>
            patchStatus( state, plan, {
                updateLoading:      true,
                updatePending:      false,
            }),

        onUpdateError: ({ updateError, plan }, { state }) =>
            patchStatus( state, plan, {
                updateError,
                updateLoading:      false,
            }),

        onUpdateResult: ( plan, { state }) => ({
            ...patchPlan( state, plan ),
            ...patchStatus( state, plan, {
                updateError:        null,
                updateLoading:      false,
            }),
        }),

        onDifferingUpdateResult: ( updateFromServer, { state }) =>
            patchStatus( state, updateFromServer, {
                updateError:        null,
                updateLoading:      false,
                updateFromServer,
            }),

        onMergeFromServerResult: ( plan, { state }) => ({
            ...patchPlan( state, getStatus( state, plan ).updateFromServer ),
            ...patchStatus( state, plan, {
                updateFromServer:   null,
            }),
        }),

        onMergeFromLocalResult: ( plan, { state }) =>
            patchStatus( state, plan, {
                updateFromServer:   null,
            }),


        /// Other ------------------------------------------------------------------

        onGetAList: ( planList, { state }) =>
            planList.reduce(
                ( state, plan ) => ({
                    ...patchPlan( state, plan ),
                    ...patchStatus( state, plan, {
                        ...statusTemplate,
                        ...state.statuses[plan.uid],
                    }),
                }),
                state,
            ),
    },
};
