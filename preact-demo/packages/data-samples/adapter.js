import debugSan         from '../debug-san/';

import { getList }      from './api';


const debug =           debugSan( 'data-samples/adapter' );


export default {

    componentDidMount: ( _, { E }) => {

        E.dataSamples.onGetList();
    },

    dataSamples: {

        onGetList: ( _, { E, getStoreState }) =>

            !getStoreState( 'dataSamples' ).getListLoading &&
                E.dataSamples.onGetListStart.async(),

        onGetListStart: ( _, { E, getStoreState }) =>

            getList( getStoreState( 'user' ).id )
                .then( debug.tapThen( 'onGetListStart' ))
                .then( E.dataSamples.onGetListResult )
                .catch( debug.tapCatch( 'onGetListStart' ))
                .catch( E.dataSamples.onGetListError ),
    },
};
