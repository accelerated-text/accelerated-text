/// Imports --------------------------------------------------------------------

require( 'dotenv-extended/config' );

const bodyParser =          require( 'body-parser' );
const cors =                require( 'cors' );
const express =             require( 'express' );

const { handler } =         require( './index' );
const mockShop =            require( './mock-shop' );


/// Constants ------------------------------------------------------------------

const LAMBDA_PATH =         '/';
const PORT =                process.env.LOCAL_PORT || 8090;

/// Main -----------------------------------------------------------------------

const app =                 express();

app.options(
    LAMBDA_PATH,
    cors(),
    ( req, res ) => {
        console.log( 'OPTIONS', LAMBDA_PATH, req.headers );
        res.send( '' );
    });

app.post(
    LAMBDA_PATH,
    bodyParser.text({ type: '*/*' }),
    ( req, res ) => {
        console.log( 'POST', LAMBDA_PATH, req.headers, req.body );
        handler( req, null, ( err, result ) => {
            console.log( 'RESULT', err, result );
            if( err ) {
                res.status( 501 ).send( err );
            } else {
                res.writeHead( result.statusCode, result.headers )
                    .end( result.body );
            }
        });
    });


if( process.argv[2] === '--mock-shop' ) {
    mockShop( app );
}

app.listen( PORT, () =>{
    console.log(
        'Server launched at:',
        `http://localhost:${ PORT }`,
    );
});
