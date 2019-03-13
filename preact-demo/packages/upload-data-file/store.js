export default {

    getInitialState: () => ({
        uploadCounter:  0,
        uploadError:    null,
        uploadFileName: null,
        uploadLoading:  false,
    }),

    uploadDataFile: {

        onUpload: ( _, { state }) => (
            state.uploadLoading && {
                uploadError:    'Will not start a new request while the previous one is not finished. Please wait.',
            }
        ),

        onUploadStart: inputFile => ({
            uploadFileName: inputFile.name,
            uploadLoading:  true,
        }),

        onUploadError: uploadError => ({
            uploadError,
            uploadLoading:  false,
        }),

        onUploadSuccess: ( _, { state }) => ({
            uploadCounter:  state.uploadCounter + 1,
            uploadError:    null,
            uploadLoading:  false,
        }),
    },
};
