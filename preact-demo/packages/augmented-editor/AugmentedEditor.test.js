const delay =           require( 'timeout-as-promise' );


const TOKEN_SELECTOR =  '.qa-blocks-token';
const INPUT_SELECTOR =  '.qa-augmented-editor-text-input';


describe( 'augmented-editor/AugmentedEditor', () => {

    beforeEach( async () => {

        await page.goto( TEST_URL, { waitUntil: 'load' });
    });

    test( 'should render text input', async () => {

        const input = await page.$( INPUT_SELECTOR );

        expect( input ).toBeTruthy();
    });

    test( 'should split text into tokens', async () => {

        await page.type( INPUT_SELECTOR, 'dog cat' );
        await page.keyboard.press( 'Enter' );

        await delay( 5e3 );
        const tokens = await page.$$( TOKEN_SELECTOR );
        expect( tokens ).toHaveLength( 2 );
    }, 10e3 );
});
