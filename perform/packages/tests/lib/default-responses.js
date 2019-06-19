import { createDataFileData }   from '../data/data-file-data';
import DATA_FILE_LIST           from '../data/data-file-list';
import DOCUMENT_PLAN_LIST       from '../data/document-plan-list';
import DICTIONARY               from '../data/dictionary';
import NLG_JOB                  from '../data/nlg-job';
import NLG_JOB_RESULT           from '../data/nlg-job-result';
import USER                     from '../data/user';


const { TEST_URL } =            process.env;


export default async ( t, run, ...args ) => {

    const {
        graphqlApi,
        onRequest: { continueAll },
        nlgApi,
    } = t;

    continueAll( 'GET', new RegExp( `${ TEST_URL }/.*` ));

    /// Start page load:
    t.timeout( 8e3 );
    const pageLoadResult =  t.page.goto( TEST_URL, { timeout: 8e3 })
        .then( t.pass, t.fail );

    /// Register these intercepts while the page is loading:
    await Promise.all([
        graphqlApi.provideOnce( 'dictionaryIds', {}, { data: DICTIONARY })
            .then(() => Promise.all( DICTIONARY.dictionary.map( dictionaryItem =>
                graphqlApi.provideOnce(
                    'dictionaryItem',
                    { id: dictionaryItem.id },
                    { data: { dictionaryItem }},
                ),
            ))),
        nlgApi.provideOnce( 'GET', `/data/?user=${ USER.id }`, DATA_FILE_LIST ),
        nlgApi.provideOnce( 'GET', '/document-plans/', DOCUMENT_PLAN_LIST )
            .then(() => Promise.all([
                nlgApi.provideOnce( 'GET', `/data/${ DATA_FILE_LIST[0].key }`, createDataFileData({
                    fieldNames: DATA_FILE_LIST[0].fieldNames,
                })),
                nlgApi.provideOnce( 'OPTIONS', '/nlg/', '' )
                    .then(() => nlgApi.provideOnce( 'POST', '/nlg/', NLG_JOB ))
                    .then(() => nlgApi.provideOnce( 'GET', `/nlg/${ NLG_JOB.resultId }`, NLG_JOB_RESULT )),
            ])),
    ]);

    await pageLoadResult;

    if( run ) {
        await run( t, ...args );
    }
};
