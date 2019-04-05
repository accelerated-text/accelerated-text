import * as nlgApi      from '../nlg-api/';


const POLL_INTERVAL =   500;
const PREFIX =          '/nlg';


const checkResult = async ( resultId, resolve, reject ) => {

    try {
        const result =  await nlgApi.GET( `${ PREFIX }/${ resultId }` );

        if( result.error ) {
            reject( result.message );
        } else if( result.ready ) {
            resolve( result );
        } else {
            setTimeout( checkResult, POLL_INTERVAL, resultId, resolve, reject );
        }
    } catch( err ) {
        reject( err );
    }
};


export const getVariants = async ({ ccg, dataId, documentPlanId }) => {

    const { resultId } = await nlgApi.POST( `${ PREFIX }/`, {
        ccg,
        dataId,
        documentPlanId,
    });

    return new Promise(( resolve, reject ) =>
        checkResult( resultId, resolve, reject )
    );
};
