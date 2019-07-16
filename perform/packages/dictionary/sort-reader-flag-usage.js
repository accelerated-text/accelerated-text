import {
    compose,
    path,
    sortBy,
    toLower,
}                           from 'ramda';


export const sortByFlagName =
    sortBy( compose( toLower, path([ 'flag', 'name' ])));


export default readerFlagUsage =>
    ( ! readerFlagUsage )
        ? []
        : sortByFlagName( readerFlagUsage );
