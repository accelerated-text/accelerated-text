const domParser =       require( 'slimdom-sax-parser' );
const fs =              require( 'fs' );
const path =            require( 'path' );
const Promise =         require( 'bluebird' );

const domToGremlin =    require( '../dom-to-gremlin' );


Promise.promisifyAll( fs );

const fileToGremlin = async fileName => {

    const fullPath =    path.resolve( __dirname, fileName );
    const xmlCode =     await fs.readFileAsync( fullPath, 'utf8' );
    const dom =         domParser.sync( xmlCode );
    return domToGremlin( dom.firstChild );
};

const matchEdge = ( type, parent, child ) =>
    new RegExp( `\\.addE\\('${ type }'\\).+'${ parent }'\\).+'${ child }'\\)`, 'm' );

const matchVertex = ( type, property ) =>
    new RegExp( `\\.addV\\('${ type }'\\).+'${ property }'\\)`, 'm' );


describe( 'blockly-gremlin/dom-to-gremlin', () => {

    test( 'should output empty string for unrecognized xml', async () => {

        const gremlinCode = await fileToGremlin( 'unrecognized.xml' );

        expect( gremlinCode ).toBeFalsy();
    });

    test( 'should parse example plan', async () => {

        const gremlinCode = await fileToGremlin( 'example-plan.xml' );

        /// Check vertices:
        expect( gremlinCode ).toMatch( matchVertex( 'segment', 'root-segment' ));
        expect( gremlinCode ).toMatch( matchVertex( 'all-words', 'first-statement' ));
        expect( gremlinCode ).toMatch( matchVertex( 'attribute', 'attribute-color' ));
        expect( gremlinCode ).toMatch( matchVertex( 'attribute', 'attribute-material' ));
        expect( gremlinCode ).toMatch( matchVertex( 'attribute', 'attribute-make' ));

        /// Check edges:
        expect( gremlinCode ).toMatch( matchEdge(
            'has-statement', 'root-segment', 'first-statement',
        ));
        expect( gremlinCode ).toMatch( matchEdge(
            'has-value', 'first-statement', 'attribute-color',
        ));
        expect( gremlinCode ).toMatch( matchEdge(
            'has-value', 'first-statement', 'attribute-material',
        ));
        expect( gremlinCode ).toMatch( matchEdge(
            'has-value', 'first-statement', 'attribute-make',
        ));

    });

    test( 'should support next vertices', async () => {

        const gremlinCode = await fileToGremlin( 'next-statement.xml' );

        /// Check vertices:
        expect( gremlinCode ).toMatch( matchVertex( 'segment', 'root-segment' ));
        expect( gremlinCode ).toMatch( matchVertex( 'all-words', 'first-list' ));
        expect( gremlinCode ).toMatch( matchVertex( 'all-words', 'second-list' ));
        expect( gremlinCode ).toMatch( matchVertex( 'all-words', 'third-list' ));

        /// Check edges:
        expect( gremlinCode ).toMatch( matchEdge(
            'has-statement', 'root-segment', 'first-list',
        ));
        expect( gremlinCode ).toMatch( matchEdge(
            'has-statement', 'root-segment', 'second-list',
        ));
        expect( gremlinCode ).toMatch( matchEdge(
            'has-statement', 'root-segment', 'third-list',
        ));
        expect( gremlinCode ).toMatch( matchEdge(
            'has-next', 'first-list', 'second-list',
        ));
        expect( gremlinCode ).toMatch( matchEdge(
            'has-next', 'second-list', 'third-list',
        ));
    });
});
