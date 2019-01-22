export default {

    getInitialState: () => ({
        plans:          [],
        selectedPlan:   null,
    }),

    planSelector: {

        onAddNew: ( name, { state }) => {
            if( !name ) {
                return;
            } else {
                const plan = {
                    id:             `${ Math.random() }`,
                    name,
                };

                return {
                    plans: [
                        plan,
                        ...state.plans,
                    ],
                    selectedPlan:   plan.id,
                };
            }
        },

        onRemoveSelected: ( _, {  state }) => {

            if( !state.selectedPlan ) {
                return;
            }

            const idx = state.plans.findIndex(
                plan => plan.id === state.selectedPlan
            );

            if( idx === -1 ) {
                return;
            }

            const plans =           [ ...state.plans ];
            plans.splice( idx, 1 );

            return {
                selectedPlan:   plans[0] && plans[0].id || null,
                plans,
            };
        },

        onRenamePlan: ({ id, name }, { state }) => {

            const idx = state.plans.findIndex(
                plan => plan.id === id
            );

            if( idx === -1 ) {
                return;
            }

            /// [...].splice() modifies the array in-place:
            const plans =   [ ...state.plans ];
            plans.splice( idx, 1, { id, name });

            return {
                plans,
            };
        },

        onSelectPlan: selectedPlan => ({
            selectedPlan,
        }),
    },
};
