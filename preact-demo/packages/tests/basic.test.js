describe( 'basic', () => {

    beforeEach( async () => {

        await page.goto( TEST_URL, { waitUntil: 'load' });
    }, 10e3 );

    test( 'should render logo', async () => {

        await expect( page ).toMatchElement( 'img[title="Augmented Writer"]' );
    });
});
