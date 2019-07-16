import { indexBy, path }    from 'ramda';


export default ( readerFlags, readerFlagUsage ) => {

    const usageMap =        indexBy( path([ 'flag', 'id' ]), readerFlagUsage );

    return readerFlags.map( flag => usageMap[ flag.id ]);
};
