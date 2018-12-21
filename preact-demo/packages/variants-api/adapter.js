import pTap                 from 'p-tap';

import variantsApi          from '../variants-api/';


export default {

    planEditor: {

        onChangeWorkspace: (
            { workspaceXml },
            { E, getStoreState }
        ) => {

            if( getStoreState( 'variantsApi' ).loading ) {
                return;
            }

            E.variantsApi.onGet.async();

            variantsApi.getForDataSample({
                dataSampleId:   'TODO_REPLACE',
                documentPlanId: 'TODO_REPLACE',
            })
                .then( E.variantsApi.onGetSuccess )
                .catch( pTap( console.error ))
                .catch( E.variantsApi.onGetError );
        },
    },
};
