import * as nlgApi      from '../nlg-api/';


const POLL_INTERVAL =   500;
const PREFIX =          '/nlg';


const checkResult = async ( resultId, resolve, reject ) => {

    try {
        const {
            error,
            message,
            ready,
            results,
        } = await nlgApi.GET( `${ PREFIX }/${ resultId }` );
        if( error ) {
            reject( message );
        } else if( ready ) {
            resolve( results );
        } else {
            setTimeout( checkResult, POLL_INTERVAL, resultId, resolve, reject );
        }
    } catch( err ) {
        reject( err );
    }
};


export const getVariants = async documentPlanId => {

    const { resultId } = await nlgApi.POST( `${ PREFIX }/`, {
        dataId:     '-1',
        documentPlanId,
    });

    return new Promise(( resolve, reject ) =>
        checkResult( resultId, resolve, reject )
    );
};
