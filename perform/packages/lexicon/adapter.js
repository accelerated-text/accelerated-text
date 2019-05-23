import { debouncedSearch }  from './api';


export default {

    /*
    componentDidMount: ( _, { E }) =>
        E.lexicon.onGet(),
    */

    lexicon: {

        onChangeQuery: ( _, { E }) =>
            E.lexicon.onGet.async(),

        onClickMore: ( _, { E }) =>
            E.lexicon.onGet.async(),

        onGet: ( _, { E, getStoreState }) => {
            const {
                query,
                requestOffset,
            } = getStoreState( 'lexicon' );

            debouncedSearch({
                offset:     requestOffset,
                query,
            })
                .then( result => E.lexicon.onGetResult({
                    ...result,
                    query,
                }))
                .catch( E.lexicon.onGetError );
        },
    },
};
