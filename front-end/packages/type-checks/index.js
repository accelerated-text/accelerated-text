import {
    flatten,
    intersection,
}                           from 'ramda';

import { ANY, UNKNOWN }     from '../nlg-blocks/types';


export const doChecksMatch = ( refCheck, check ) => (
    ( refCheck === UNKNOWN && check === UNKNOWN )
    || ( refCheck !== UNKNOWN && check !== UNKNOWN && (
        refCheck === ANY
        || check === ANY
        || ( refCheck instanceof Array
            ? refCheck.includes( ANY )
                ? true
                : check instanceof Array
                    ? intersection( refCheck, check ).length
                    : refCheck.includes( check )
            : check instanceof Array
                ? check.includes( refCheck )
                : check === refCheck
        )
    ))
);


export const checkConnection = ( connection, check ) =>
    doChecksMatch( connection.getCheck(), check );


export const checkConnections = ( connections, check ) =>
    connections.find( connection =>
        checkConnection( connection, check )
    );


export const getTypeArgs = type =>
    Object.entries( type.json )
        .filter(([ key ]) =>
            key.match( /^args\d+$/ )
        ).map(([ _, value ]) =>
            value
        );

export const getStatementChecks = type =>
    flatten( getTypeArgs( type ))
        .filter(({ type }) => type === 'input_statement' )
        .map(({ check }) => check )
        .filter( check => check !== undefined );


export const getValueChecks = type =>
    flatten( getTypeArgs( type ))
        .filter(({ type }) => type === 'input_value' )
        .map(({ check }) => check )
        .filter( check => check !== undefined );


export const countAvailableStatements = ( connections, type ) => {
    const checks =          getStatementChecks( type );
    let count =             0;
    for( const conn of connections ) {
        const idx = checks.findIndex( check =>
            checkConnection( conn, check )
        );
        if( idx > -1 ) {
            checks.splice( idx, 1 );    /// delete item at idx
            count +=        1;
        }
    }
    return count;
};


export const countAvailableValues = ( connections, type ) => {
    const checks =          getValueChecks( type );
    let count =             0;
    for( const conn of connections ) {
        if( type.valueListCheck !== UNKNOWN
            && checkConnection( conn, type.valueListCheck )
        ) {
            count +=        1;
        } else {
            const idx = checks.findIndex( check =>
                checkConnection( conn, check )
            );
            if( idx > -1 ) {
                checks.splice( idx, 1 );    /// delete item at idx
                count +=    1;
            }
        }
    }
    return count;
};
