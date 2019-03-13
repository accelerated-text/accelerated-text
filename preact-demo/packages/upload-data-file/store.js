export default {

    getInitialState: () => ({
        uploadError:    null,
        uploadLoading:  false,
    }),

    uploadDataFile: {

        onUpload: ( _, { state }) => (
            state.uploadLoading && {
                uploadError:    'Will not start a new request while the previous one is not finished. Please wait.',
            }
        ),

        onUploadStart: () => ({
            uploadLoading:  true,
        }),

        onUploadError: uploadError => ({
            uploadError,
            uploadLoading:  false,
        }),

        onUploadSuccess: () => ({
            uploadError:    null,
            uploadLoading:  false,
        }),
    },
};
