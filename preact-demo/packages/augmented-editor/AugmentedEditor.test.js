const delay =           require( 'timeout-as-promise' );


const WORD_SELECTOR =  '.qa-blocks-word';
const INPUT_SELECTOR =  '.qa-augmented-editor-text-input';


describe( 'augmented-editor/AugmentedEditor', () => {

    beforeEach( async () => {

        await page.goto( TEST_URL, { waitUntil: 'load' });
    });

    test( 'should render text input', async () => {

        const input = await page.$( INPUT_SELECTOR );

        expect( input ).toBeTruthy();
    });

    test( 'should split text into words', async () => {

        await page.type( INPUT_SELECTOR, 'one two' );
        await page.keyboard.press( 'Enter' );

        await delay( 500 );
        const words = await page.$$( WORD_SELECTOR );
        expect( words ).toHaveLength( 2 );
    });
});
