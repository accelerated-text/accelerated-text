import { getList }      from './api';


export default {

    componentDidMount: ( _, { E }) => {

        E.contexts.onGetList();
    },

    contexts: {

        onGetList: ( _, { E, getStoreState }) =>

            !getStoreState( 'contexts' ).getListLoading &&
                E.contexts.onGetListStart.async(),

        onGetListStart: ( _, { E }) =>

            getList()
                .then( E.contexts.onGetListResult )
                .catch( E.contexts.onGetListError ),
    },
};
