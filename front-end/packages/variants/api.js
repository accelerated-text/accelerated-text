import * as nlgApi      from '../nlg-api/';


const POLL_INTERVAL =   500;
const PREFIX =          '/nlg';
var LAST_STATUS_CHECK = 0;
var LAST_RESULT = true;

export const checkStatus = async () => {
    let now = new Date().valueOf();
    if(now - LAST_STATUS_CHECK < POLL_INTERVAL * 5){
        return LAST_RESULT;
    }

    LAST_STATUS_CHECK = new Date().valueOf();
    try {
        const result = await nlgApi.GET("/status");

        if( result.color == "green" ) {
            LAST_RESULT = true;
        }
        else {
            LAST_RESULT = false;
        }
        return LAST_RESULT;
    } catch ( err ) {
        console.log(err);
        LAST_RESULT = false;
        return false;
    }
};


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


export const getVariants = async ({ dataRow, documentPlanId, readerFlagValues }) => {
    const { resultId } = await nlgApi.POST( `${ PREFIX }/`, {
        dataRow,
        documentPlanId,
        readerFlagValues
    });

    return new Promise(( resolve, reject ) =>
        checkResult( resultId, resolve, reject )
    );
};
