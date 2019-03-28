const noRecords =           require( './response-templates/no-records' );


describe( 'basic', () => {

    beforeEach(() => noRecords( page ), 10e3 );

    test( 'should render logo', async () => {

        await expect( page ).toMatchElement( 'img[title="Accelerated Text"]' );
    });
});
