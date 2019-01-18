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

const matchEdgeFrom = ( type, parent ) =>
    new RegExp( `\\.addE\\('${ type }'\\)\\.from\\(.+'${ parent }'\\)\\.next\\(\\)\\).to\\(`, 'm' );

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

    test( 'should support next_values mutation', async () => {

        const gremlinCode = await fileToGremlin( 'next-values-mutation.xml' );
        const toMatch =     expect( gremlinCode ).toMatch;
        const notToMatch =  expect( gremlinCode ).not.toMatch;

        toMatch( matchVertex( 'segment', 'root-segment' ));
        toMatch( matchVertex( 'value-block', 'ignore-1' ));
        toMatch( matchVertex( 'value-block', 'ignore-2' ));
        toMatch( matchVertex( 'value-block', 'value-1' ));
        toMatch( matchVertex( 'value-block', 'value-2' ));
        toMatch( matchVertex( 'value-block', 'value-3' ));

        toMatch( matchEdge( 'has-next', 'value-1', 'value-2' ));
        toMatch( matchEdge( 'has-next', 'value-2', 'value-3' ));

        notToMatch( matchEdge( 'has-next', 'ignore-1', 'value-1' ));
        notToMatch( matchEdge( 'has-next', 'value-2', 'ignore-2' ));
        notToMatch( matchEdge( 'has-next', 'ignore-2', 'value-3' ));

        notToMatch( matchEdge( 'has-next', 'ignore-1', 'ignore-2' ));
    });

    test( 'should support else_if_count mutation', async () => {

        const gremlinCode = await fileToGremlin( 'else-if-count-mutation.xml' );
        const toMatch =     expect( gremlinCode ).toMatch;
        const notToMatch =  expect( gremlinCode ).not.toMatch;

        let TEST_ID;

        toMatch( matchVertex( 'test', 'empty' ));
        notToMatch( matchEdgeFrom( 'has-condition', 'empty' ));

        toMatch( matchVertex( 'test', 'empty-long' ));
        notToMatch( matchEdgeFrom( 'has-condition', 'empty-long' ));

        toMatch( matchVertex( 'test', 'only-if' ));
        toMatch( matchVertex( 'value-block', 'only-if-if-value' ));
        toMatch( matchEdge( 'has-condition', 'only-if', 'only-if-if-value' ));
        notToMatch( matchEdgeFrom( 'then-expression', 'only-if-if-value' ));
        notToMatch( matchEdgeFrom( 'has-next', 'only-if-if-value' ));

        toMatch( matchVertex( 'test', 'if-then' ));
        toMatch( matchVertex( 'value-block', 'if-then-if-value' ));
        toMatch( matchVertex( 'value-block', 'if-then-then-value' ));
        toMatch( matchEdge( 'has-condition', 'if-then', 'if-then-if-value' ));
        toMatch( matchEdge( 'then-expression', 'if-then-if-value', 'if-then-then-value' ));
        notToMatch( matchEdgeFrom( 'has-next', 'if-then-if-value' ));

        toMatch( matchVertex( 'test', 'if-then-else' ));
        toMatch( matchVertex( 'value-block', 'if-then-else-if-value' ));
        toMatch( matchVertex( 'value-block', 'if-then-else-then-value' ));
        toMatch( matchVertex( 'value-block', 'if-then-else-else-value' ));
        toMatch( matchEdge( 'has-condition', 'if-then-else', 'if-then-else-if-value' ));
        toMatch( matchEdge( 'has-condition', 'if-then-else', 'if-then-else-else-value' ));
        toMatch( matchEdge( 'then-expression', 'if-then-else-if-value', 'if-then-else-then-value' ));
        notToMatch( matchEdgeFrom( 'then-expression', 'if-then-else-else-value' ));
        toMatch( matchEdge( 'has-next', 'if-then-else-if-value', 'if-then-else-else-value' ));
        notToMatch( matchEdgeFrom( 'has-next', 'if-then-else-else-value' ));

        /// TODO: add more cases.

        TEST_ID =   'if-then-elseif-then-else';
        toMatch( matchVertex( 'test', TEST_ID ));
        toMatch( matchVertex( 'value-block', `${ TEST_ID }-if-value` ));
        toMatch( matchVertex( 'value-block', `${ TEST_ID }-then-value` ));
        toMatch( matchVertex( 'value-block', `${ TEST_ID }-elseif-value-0` ));
        toMatch( matchVertex( 'value-block', `${ TEST_ID }-then-value-0` ));
        toMatch( matchVertex( 'value-block', `${ TEST_ID }-else-value` ));
        toMatch( matchEdge( 'has-condition', TEST_ID, `${ TEST_ID }-if-value` ));
        toMatch( matchEdge( 'has-condition', TEST_ID, `${ TEST_ID }-elseif-value-0` ));
        toMatch( matchEdge( 'has-condition', TEST_ID, `${ TEST_ID }-else-value` ));
        toMatch( matchEdge( 'then-expression', `${ TEST_ID }-if-value`, `${ TEST_ID }-then-value` ));
        toMatch( matchEdge( 'then-expression', `${ TEST_ID }-elseif-value-0`, `${ TEST_ID }-then-value-0` ));
        notToMatch( matchEdgeFrom( 'then-expression', `${ TEST_ID }-else-value` ));
        toMatch( matchEdge( 'has-next', `${ TEST_ID }-if-value`, `${ TEST_ID }-elseif-value-0` ));
        toMatch( matchEdge( 'has-next', `${ TEST_ID }-elseif-value-0`, `${ TEST_ID }-else-value` ));
        notToMatch( matchEdgeFrom( 'has-next', `${ TEST_ID }-else-value` ));

    });
});
