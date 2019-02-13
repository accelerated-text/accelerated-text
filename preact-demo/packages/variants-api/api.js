import * as nlgApi      from '../nlg-api/';


const PREFIX =          '/document-plans';


export const getVariants = planId =>
    nlgApi.GET( `${ PREFIX }/${ planId }/variants` );
