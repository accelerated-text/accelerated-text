import { h, Component }     from 'preact';

import { composeQueries }   from '../graphql/';
import {
    createDocumentPlan,
    deleteDocumentPlan,
    updateDocumentPlan,
}                           from '../graphql/mutations.graphql';
import OpenedPlanContext    from '../amr/OpenedPlanContext';

import {
    preparePlanJson,
}                           from './functions';
import planTemplate from './paradigms-plan-template';
import uuid from 'uuid';

export const createPlan = fields => ({
    ...planTemplate,
    ...fields,
    createdAt:          +new Date,
    id:                 undefined,
    uid:                uuid.v4(),
});


export default ChildComponent =>
    composeQueries({
        createDocumentPlan,
        deleteDocumentPlan,
        updateDocumentPlan,
    })( class DocumentPlansPlanActions extends Component {

        static contextType =            OpenedPlanContext;

        state = {
            createError:                null,
            createLoading:              false,
            deleteError:                null,
            deleteLoading:              false,
            updateError:                null,
            updateLoading:              false,
        };

        onCreate = plan => {
            this.setState({
                createLoading:          true,
            });
            const createdPlan =         createPlan( plan );
            this.context.openPlan( createdPlan );
            return this.props.createDocumentPlan({
                variables:              preparePlanJson( createdPlan ),
                refetchQueries:         [ 'amrPlans' ],
                optimisticResponse: {
                    __typename:         'Mutation',
                    createDocumentPlan: {
                        ...createdPlan,
                        id:             Math.random().toString( 36 ),
                        createdAt:      null,
                        updatedAt:      null,
                    },
                },
            }).then( mutationResult => {
                this.setState({
                    createError:        mutationResult.error,
                    createLoading:      false,
                });
                return mutationResult;
            });
        };

        onDelete = plan => {
            this.setState({
                deleteLoading:          true,
            });
            return this.props.deleteDocumentPlan({
                variables: {
                    id:                 plan.id,
                },
                refetchQueries:         [ 'documentPlans' ],
            }).then( mutationResult => {
                this.setState({
                    deleteError:        mutationResult.error,
                    deleteLoading:      false,
                });
                return mutationResult;
            });
        };

        onUpdate = plan => {
            this.setState({
                updateLoading:          true,
            });
            return this.props.updateDocumentPlan({
                variables:              preparePlanJson( plan ),
                optimisticResponse: {
                    __typename:         'Mutation',
                    updateDocumentPlan: plan,
                },
            }).then( mutationResult => {
                this.setState({
                    updateError:        mutationResult.error,
                    updateLoading:      false,
                });
                return mutationResult;
            });
        };

        render = ( props, state ) =>
            <ChildComponent
                onCreatePlan={ this.onCreate }
                onDeletePlan={ this.onDelete }
                onUpdatePlan={ this.onUpdate }
                planStatus={ state }
                { ...props }
            />;
    });
