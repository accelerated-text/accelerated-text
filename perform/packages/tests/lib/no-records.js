import { EMPTY_DICTIONARY } from '../data/dictionary';
import { EMPTY_RFLAGS }     from '../data/reader-flags';
import USER                 from '../data/user';


const { TEST_URL } =        process.env;


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

    await Promise.all([
        nlgApi.provideOnce( 'GET', `/data/?user=${ USER.id }`, []),
        nlgApi.provideOnce( 'GET', '/document-plans/', []),
        graphqlApi.provideOnce( 'dictionary', {}, { data: EMPTY_DICTIONARY }),
        graphqlApi.provideOnce( 'readerFlags', {}, { data: EMPTY_RFLAGS }),
    ]);

    await pageLoadResult;

    if( run ) {
        await run( t, ...args );
    }
};
