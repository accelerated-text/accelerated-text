export default {

    getInitialState: () => ({
        uploadCounter:      0,
        uploadError:        null,
        uploadFileKey:      null,
        uploadForPlanUid:   null,
        uploadLoading:      false,
    }),

    uploadDataFile: {

        onUpload: ( _, { state }) => (
            state.uploadLoading && {
                uploadError:    'Will not start a new request while the previous one is not finished. Please wait.',
            }
        ),

        onUploadStart: ( inputFile, { getStoreState }) => ({
            uploadFileKey:      `${ getStoreState( 'user' ).id }/${ inputFile.name }`,
            uploadForPlanUid:   getStoreState( 'planList' ).openedPlanUid,
            uploadLoading:      true,
        }),

        onUploadError: uploadError => ({
            uploadError,
            uploadFileKey:      null,
            uploadForPlanUid:   null,
            uploadLoading:      false,
        }),

        onUploadFileSuccess: ( _, { state }) => ({
            uploadCounter:      state.uploadCounter + 1,
            uploadError:        null,
            uploadLoading:      false,
        }),

        onUploadSyncSuccess: () => ({
            uploadFileKey:      null,
            uploadForPlanUid:   null,
        }),
    },
};
