import * as nlgApi      from '../nlg-api/';


const POLL_INTERVAL =   500;
const PREFIX =          '/nlg';


const checkResult = async ( resultId, resolve, reject ) => {

    const { ready, results } =
        await nlgApi.GET( `${ PREFIX }/${ resultId }` );
    if( ready ) {
        resolve( results );
    } else {
        setTimeout( checkResult, POLL_INTERVAL, resultId, resolve, reject );
    }
};


export const getVariants = async documentId => {

    const { resultId } = await nlgApi.POST( `${ PREFIX }/`, {
        dataId:     '-1',
        documentId,
    });

    return new Promise(( resolve, reject ) =>
        checkResult( resultId, resolve, reject )
    );
};
