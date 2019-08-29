import debugSan             from '../debug-san/';
import {
    getPlanByUid,
    getStatusByUid,
}                           from '../document-plans/functions';
import {
    getDataFile,
    listDataFiles,
}                           from '../graphql/queries.graphql';

import uploadToS3           from './upload-to-s3';


const debug =               debugSan( 'upload-data-file/adapter' );


export default {

    uploadDataFile: {

        onUpload: ( inputFile, { E, getStoreState }) => {

            if( !getStoreState( 'uploadDataFile' ).uploadLoading ) {
                E.uploadDataFile.onUploadStart.async( inputFile );
            }
        },

        onUploadStart: ( inputFile, { E, getStoreState, props }) => {

            const {
                uploadFileKey,
                uploadForPlanUid,
            } = getStoreState( 'uploadDataFile' );

            uploadToS3( uploadFileKey, inputFile )
                .then( debug.tapThen( 'onUploadStart' ))
                .then( E.uploadDataFile.onUploadFileSuccess )
                .then(() => props.client.query({
                    fetchPolicy:            'network-only',
                    query:                  listDataFiles,
                }))
                .then(({ error, data, loading }) => {

                    if( error ) {
                        throw Error( error );
                    }

                    const isInList = (
                        data
                            .listDataFiles
                            .dataFiles
                            .find(({ id }) => id === uploadFileKey )
                    );

                    const documentPlans =   getStoreState( 'documentPlans' );
                    const plan =            getPlanByUid( documentPlans, uploadForPlanUid );
                    const planStatus =      getStatusByUid( documentPlans, uploadForPlanUid );

                    if( !isInList ) {
                        throw Error( 'Failed to update the data file list.' );
                    } else if( !plan ) {
                        throw Error( 'Document plan is gone.' );
                    } else if( planStatus.isDeleted ) {
                        throw Error( 'Document plan is deleted.' );
                    } else if( plan.dataSampleId === uploadFileKey ) {
                        E.variantsApi.onGet();
                    } else {
                        E.documentPlans.onUpdate({
                            ...plan,
                            dataSampleId:   uploadFileKey,
                            dataSampleRow:  0,
                        });
                    }
                })
                .then( E.uploadDataFile.onUploadSyncSuccess )
                .then(() => props.client.query({
                    fetchPolicy:            'network-only',
                    query:                  getDataFile,
                    variables: { id:        uploadFileKey },
                }))
                .then( E.uploadDataFile.onUploadDone )
                .catch( debug.tapCatch( 'onUploadStart' ))
                .catch( E.uploadDataFile.onUploadError );
        },

        onUploadDone: ( _, { props }) =>
            props.onUploadDone && props.onUploadDone(),
    },
};
