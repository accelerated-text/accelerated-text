import uploadToS3       from './upload-to-s3';


const USER_ID =         'example-user';


export default {

    uploadDataFile: {

        onUpload: ( inputFile, { E, getStoreState }) => {

            if( !getStoreState( 'uploadDataFile' ).uploadLoading ) {
                E.uploadDataFile.onUploadStart.async( inputFile );
            }
        },

        onUploadStart: ( inputFile, { E }) => {

            const key = `${ USER_ID }/${ inputFile.name }`;
            uploadToS3( key, inputFile )
                .then( E.uploadDataFile.onUploadSuccess )
                .catch( E.uploadDataFile.onUploadError );
        },
    },
};
