import uploadToS3       from './upload-to-s3';


export default {

    uploadDataFile: {

        onUpload: ( inputFile, { E, getStoreState }) => {

            if( !getStoreState( 'uploadDataFile' ).uploadLoading ) {
                E.uploadDataFile.onUploadStart.async( inputFile );
            }
        },

        onUploadStart: ( inputFile, { E }) => {

            uploadToS3( inputFile )
                .then( E.uploadDataFile.onUploadSuccess )
                .catch( E.uploadDataFile.onUploadError );
        },
    },
};
