import uuid             from 'uuid';

import planTemplate     from './plan-template';

const EXECUTE =         'execute';
const WAIT =            'wait';

const STATUS_TEMPLATE = {
    isDeleted:          false,
    isNew:              false,

    createError:        null,
    createLoading:      false,
    deleteError:        null,
    deleteLoading:      false,
    deletePending:      false,
    readError:          null,
    readLoading:        false,
    updateError:        null,
    updateLoading:      false,
    updatePending:      false,
    updateFromServer:   null,
};

const patchUpdateCount = ( container, plan ) => ({
    updateCount:        1 + ( container[plan.uid] && container[plan.uid].updateCount || 0 ),
});

const patchPlan = ( state, plan ) => ({
    plans: {
        ...state.plans,
        [plan.uid]: {
            ...state.plans[plan.uid],
            ...plan,
            ...patchUpdateCount( state.plans, plan ),
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

    plans: {
        /// Create -----------------------------------------------------------------

        onCreate: ( someFields, { state }) => {
            if( !someFields || !someFields.name ) {
                return;
            }

            const createdPlan = {
                ...planTemplate,
                ...someFields,
                createdAt:      +new Date,
                uid:            uuid.v4(),
            };

            return {
                ...patchPlan( state, createdPlan ),
                ...patchStatus( state, createdPlan, {
                    ...STATUS_TEMPLATE,
                    isNew:      true,
                }),
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
                isNew:          false,
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
            } = state.statuses[plan.uid];

            if( deleteLoading || deletePending ) {
                return;
            }

            return {
                ...patchStatus( state, plan, {
                    deletePending:  !!createLoading,
                    updatePending:  false,
                    isDeleted:      true,
                }),
            };
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
                isNew,
                createLoading,
                deleteLoading,
                deletePending,
                updateLoading,
                updatePending,
            } = state.statuses[plan.uid];

            const shouldSkip = (
                isDeleted
                || deleteLoading
                || deletePending
            );

            const shouldWait = (
                isNew
                || createLoading
                || updateLoading
                || updatePending
            );

            if( shouldSkip ) {
                return;
            }

            return {
                ...patchPlan( state, plan ),
                ...patchStatus( state, plan, {
                    updatePending:  !!shouldWait,
                }),
            };
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
            ...patchPlan( state, state.statuses[plan.uid].updateFromServer ),
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
            planList.reduce( patchPlan, state ),
    },
};
