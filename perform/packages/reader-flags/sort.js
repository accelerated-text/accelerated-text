import {
    compose,
    prop,
    sortBy,
    toLower,
}                           from 'ramda';


export const sortByName =   sortBy( compose( toLower, prop( 'name' )));


export default readerFlags =>
    ( ! readerFlags || ! readerFlags.flags )
        ? []
        : sortByName( readerFlags.flags );
