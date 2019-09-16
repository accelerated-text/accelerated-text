import { range }            from 'ramda';

import { createDocumentPlan }   from './document-plan';


const PREFIX =              'tests-data-document-plan-list';


export const createDocumentPlans = totalCount => ({
    documentPlans: {
        __typename:     'DocumentPlanResults',
        offset:         0,
        totalCount,
        limit:          totalCount,
        items: range( 0, totalCount ).map( i =>
            createDocumentPlan( `${ PREFIX }-${ i }` )
        ),
    },
});


export const EMPTY_DOCUMENT_PLANS = createDocumentPlans( 0 );


export default createDocumentPlans( 3 );
