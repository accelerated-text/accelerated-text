const debugConsole =        require( '../../qa-utils/debug-console' );
const nlgProvide =          require( '../../nlg-api/provide-response' );
const requestInterceptor =  require( '../../qa-utils/request-interceptor' );

const DATA_FILE =           require( '../data/data-file' );
const DOCUMENT_PLAN =       require( '../data/document-plan' );
const LEXICON_LIST =        require( '../data/lexicon-list' );
const NLG_JOB =             require( '../data/nlg-job' );
const NLG_JOB_RESULT =      require( '../data/nlg-job-result' );
const USER =                require( '../data/user' );


const { TEST_URL } =        process.env;


module.exports = async page => {

    debugConsole( page );

    const interceptor =     await requestInterceptor( page );
    const {
        continueAll,
        provideOnce,
        stopInterception,
    } = interceptor;
    const nlgProvideOnce =  nlgProvide( provideOnce );

    continueAll( 'GET', new RegExp( `${ TEST_URL }/.*` ));

    page.goto( TEST_URL );

    await Promise.all([
        nlgProvideOnce( 'GET', `/data/?user=${ USER.id }`, [ DATA_FILE ]),
        nlgProvideOnce( 'GET', '/lexicon?', LEXICON_LIST ),
        nlgProvideOnce( 'GET', '/document-plans/', [ DOCUMENT_PLAN ])
            .then(() => nlgProvideOnce( 'POST', '/nlg/', NLG_JOB ))
            .then(() => nlgProvideOnce( 'GET', `/nlg/${ NLG_JOB.resultId }`, NLG_JOB_RESULT )),
    ]);

    await stopInterception( page );

    return {
        ...interceptor,
        nlgProvideOnce,
    };
};
