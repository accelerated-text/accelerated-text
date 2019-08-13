const clearResult = () => ({
    result:             null,
});


const clearIfNewPlan = ( _, { getStoreState }) => {

    const {
        openedPlanUid,
        previousOpenedPlanUid,
    } = getStoreState( 'planList' );

    return (
        openedPlanUid !== previousOpenedPlanUid
            ? {
                error:  false,
                result: null,
            }
            : null
    );
};


export default {

    getInitialState: () => ({
        error:          false,
        loading:        false,
        pending:        false,
        result:         null,
    }),

    documentPlans: {

        onCreate:           clearResult,
        onDelete:           clearResult,
    },

    planList: {
        onSelectPlan:       clearIfNewPlan,
        onGetListResult:    clearIfNewPlan,
    },

    variantsApi: {

        onGet: ( _, { state }) => (
            state.loading
                ? { pending: true }
                : null
        ),

        onGetStart: () => ({
            loading:    true,
            pending:    false,
        }),

        onGetAbort: () => ({
            loading:    false,
        }),

        onGetError: error => ({
            error,
            loading:    false,
        }),

        onGetResult: result => ({
            error:      false,
            loading:    false,
            result,
        }),
    },
};
