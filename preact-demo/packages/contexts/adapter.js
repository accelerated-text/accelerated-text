import debugSan         from '../debug-san/';

import { getList }      from './api';


const debug =           debugSan( 'contexts/adapter' );


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
                .then( debug.tapThen( 'onGetListStart' ))
                .then( E.contexts.onGetListResult )
                .catch( debug.tapCatch( 'onGetListStart' ))
                .catch( E.contexts.onGetListError ),
    },
};
