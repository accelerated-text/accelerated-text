import EMPTY_LEXICON_LIST   from '../data/empty-lexicon-list';
import USER                 from '../data/user';


const { TEST_URL } =        process.env;


export default async ( t, run, ...args ) => {

    const {
        interceptor: { continueAll },
        nlgApi,
    } = t;

    continueAll( 'GET', new RegExp( `${ TEST_URL }/.*` ));

    /// Start page load:
    t.timeout( 8e3 );
    const pageLoadResult =  t.page.goto( TEST_URL, { timeout: 8e3 })
        .then( t.pass, t.fail );

    await Promise.all([
        nlgApi.provideOnce( 'GET', `/data/?user=${ USER.id }`, []),
        nlgApi.provideOnce( 'GET', '/document-plans/', []),
        nlgApi.provideOnce( 'GET', '/lexicon?', EMPTY_LEXICON_LIST ),
    ]);

    await pageLoadResult;

    if( run ) {
        await run( t, ...args );
    }
};
