import * as dataSamplesApi  from '../data-samples/api';
import debugSan         from '../debug-san/';

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
                uploadForPlan,
            } = getStoreState( 'uploadDataFile' );

            uploadToS3( uploadFileKey, inputFile )
                .then( debug.tapThen( 'onUploadStart' ))
                .then(  E.uploadDataFile.onUploadSuccess )
                .then(() => dataSamplesApi.getList())
                .then( files => {
                    E.dataSamples.onGetListResult( files );

                    const isInList =    files.find(({ id }) => id === uploadFileKey );
                    const plan =        getStoreState( 'documentPlans' )[uploadForPlan];

                    if( !isInList ) {
                        throw Error( 'Failed to update the data file list.' );
                    } else if( !plan ) {
                        throw Error( 'Document plan is gone.' );
                    } else {
                        E.documentPlans.onUpdate({
                            ...plan,
                            dataId: uploadFileKey,
                        });
                    }
                })
                .then( E.uploadDataFile.onUploadSyncSuccess )
                .catch( debug.tapCatch( 'onUploadStart' ))
                .catch( E.uploadDataFile.onUploadError );
        },
    },
};
