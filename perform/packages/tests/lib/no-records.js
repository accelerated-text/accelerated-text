import EMPTY_LEXICON_LIST   from '../data/empty-lexicon-list';
import USER                 from '../data/user';


const { TEST_URL } =        process.env;


export default async ( t, run, ...args ) => {

    const {
        interceptor: { continueAll },
        nlgProvideOnce,
    } = t;

    continueAll( 'GET', new RegExp( `${ TEST_URL }/.*` ));

    /// Start page load:
    t.timeout( 8e3 );
    const pageLoadResult =  t.page.goto( TEST_URL, { timeout: 8e3 })
        .then( t.pass, t.fail );

    await Promise.all([
        nlgProvideOnce( 'GET', `/data/?user=${ USER.id }`, []),
        nlgProvideOnce( 'GET', '/document-plans/', []),
        nlgProvideOnce( 'GET', '/lexicon?', EMPTY_LEXICON_LIST ),
    ]);

    await pageLoadResult;

    if( run ) {
        await run( t, ...args );
    }
};
