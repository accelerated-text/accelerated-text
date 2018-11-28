describe( 'app/App', () => {

    beforeEach( async () => {

        await page.goto( TEST_URL, { waitUntil: 'load' });
    });

    test( 'should render title', async () => {

        await expect( page ).toMatch( 'Augmented Writer' );
    });
});
