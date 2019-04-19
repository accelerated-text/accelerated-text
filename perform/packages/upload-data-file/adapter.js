import * as dataSamplesApi  from '../data-samples/api';
import debugSan         from '../debug-san/';
import {
    getPlanByUid,
    getStatusByUid,
}   from '../document-plans/functions';

import uploadToS3       from './upload-to-s3';


const debug =           debugSan( 'upload-data-file/adapter' );


export default {

    uploadDataFile: {

        onUpload: ( inputFile, { E, getStoreState }) => {

            if( !getStoreState( 'uploadDataFile' ).uploadLoading ) {
                E.uploadDataFile.onUploadStart.async( inputFile );
            }
        },

        onUploadStart: ( inputFile, { E, getStoreState }) => {

            const {
                uploadFileKey,
                uploadForPlanUid,
            } = getStoreState( 'uploadDataFile' );

            uploadToS3( uploadFileKey, inputFile )
                .then( debug.tapThen( 'onUploadStart' ))
                .then( E.uploadDataFile.onUploadFileSuccess )
                .then(() => dataSamplesApi.getList( getStoreState( 'user' ).id ))
                .then( files => {
                    E.dataSamples.onGetListResult( files );

                    const isInList =        files.find(({ id }) => id === uploadFileKey );

                    const documentPlans =   getStoreState( 'documentPlans' );
                    const plan =            getPlanByUid( documentPlans, uploadForPlanUid );
                    const planStatus =      getStatusByUid( documentPlans, uploadForPlanUid );

                    if( !isInList ) {
                        throw Error( 'Failed to update the data file list.' );
                    } else if( !plan || plan.isDeleted ) {
                        throw Error( 'Document plan is gone.' );
                    } else if( planStatus.isDeleted ) {
                        throw Error( 'Document plan is deleted.' );
                    } else {
                        E.documentPlans.onUpdate({
                            ...plan,
                            dataSampleId:   uploadFileKey,
                        });
                    }
                })
                .then( E.uploadDataFile.onUploadSyncSuccess )
                .catch( debug.tapCatch( 'onUploadStart' ))
                .catch( E.uploadDataFile.onUploadError );
        },
    },
};
