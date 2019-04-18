import {
    patchItem,
    patchStatus,
    statusTemplate,
}   from './functions';


export default {

    getInitialState: () => ({
        fileIds:        null,
        fileItems:      {},
        getListError:   null,
        getListLoading: false,
        statuses:       {},
    }),

    dataSamples: {

        onGetList: ( _, { state }) => (
            state.getListLoading && {
                getListError:   'Will not start a new request while the previous one is not finished. Please wait.',
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
