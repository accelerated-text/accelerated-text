import debugSan         from '../debug-san/';

import { GET }          from './api';


const debug =           debugSan( 'data-samples/adapter' );

const fixListResult = list =>
    list.filter( item => item.fieldNames && item.fieldNames.length > 1 )
        .map(( item, i ) => ({
            ...item,
            contentType:    item.contentType || 'text/csv',
            id:             item.key,
            fileName:       `file0${ i }.csv`,
        }));


export default {

    componentDidMount: ( _, { E }) => {

        E.dataSamples.onGetList();
    },

    dataSamples: {

        onGetList: ( _, { E, getStoreState }) =>

            !getStoreState( 'dataSamples' ).getListLoading &&
                E.dataSamples.onGetListStart.async(),

        onGetListStart: ( _, { E }) =>

            GET( '/' )
                .then( debug.tapThen( 'onGetListStart' ))
                .then( fixListResult )
                .then( debug.tapThen( 'onGetListStart resultFixed' ))
                .then( E.dataSamples.onGetListResult )
                .catch( debug.tapCatch( 'onGetListStart' ))
                .catch( E.dataSamples.onGetListError ),
    },
};
