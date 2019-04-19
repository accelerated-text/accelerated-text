import {
    getStatus,
    patchItem,
    patchStatus,
    statusTemplate,
}   from './functions';


const REQUEST_NOT_FINISHED_ERROR =  'Will not start a new request while the previous one is not finished. Please wait.';


export default {

    getInitialState: () => ({
        fileIds:        [],
        fileItems:      {},
        getListError:   null,
        getListLoading: false,
        statuses:       {},
    }),

    dataSamples: {

        /// Item events

        onGetData: ( fileItem, { state }) => (
            getStatus( state, fileItem ).getDataLoading
                && patchStatus( state, fileItem, {
                    getDataError:   REQUEST_NOT_FINISHED_ERROR,
                })
        ),

        onGetDataStart: ( fileItem, { state }) =>
            patchStatus( state, fileItem, {
                getDataLoading:     true,
            }),

        onGetDataError: ({ fileItem, getDataError }, { state }) =>
            patchStatus( state, fileItem, {
                getDataError,
                getDataLoading:     false,
            }),

        onGetDataResult: ({ fileItem, data }, { state }) => ({
            ...patchItem( state, {
                ...fileItem,
                data,
            }),
            ...patchStatus( state, fileItem, {
                getDataError:       null,
                getDataLoading:     false,
            }),
        }),

        /// List events

        onGetList: ( _, { state }) => (
            state.getListLoading && {
                getListError:       REQUEST_NOT_FINISHED_ERROR,
            }
        ),

        onGetListStart: () => ({
            getListLoading: true,
        }),

        onGetListError: getListError => ({
            getListError,
            getListLoading: false,
        }),

        onGetListResult: ( files, { state }) => ({
            ...files.reduce(
                ( state, file ) => ({
                    ...patchItem( state, file ),
                    ...patchStatus( state, file, {
                        ...statusTemplate,
                        ...state.statuses[file.id],
                    }),
                }),
                state,
            ),
            fileIds:        files.map(({ id }) => id ),
            getListError:   null,
            getListLoading: false,
        }),
    },
};
