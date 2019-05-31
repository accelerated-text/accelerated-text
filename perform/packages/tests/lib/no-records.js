import EMPTY_LEXICON_LIST   from '../data/empty-lexicon-list';
import USER                 from '../data/user';


const { TEST_URL } =        process.env;


export default async ( t, run, ...args ) => {

    const {
        graphQL,
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
        graphQL.provideOnce({ data: { results: EMPTY_LEXICON_LIST }}),
    ]);

    await pageLoadResult;

    if( run ) {
        await run( t, ...args );
    }
};
