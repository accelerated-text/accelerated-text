/// Imports --------------------------------------------------------------------

require( 'dotenv-extended/config' );

const bodyParser =          require( 'body-parser' );
const cors =                require( 'cors' );
const express =             require( 'express' );

const corsLambda =          require( './cors-lambda/' );
const mockShopLambda =      require( './mock-shop-lambda/' );


/// Constants ------------------------------------------------------------------

const CORS_LAMBDA_PATH =    '/';
const MOCK_LAMBDA_PATH =    '/mock-shop';
const PORT =                process.env.LOCAL_PORT || 8090;

/// Main -----------------------------------------------------------------------

const app =                 express();

app.options(
    CORS_LAMBDA_PATH,
    cors(),
    ( req, res ) => {
        console.log( 'OPTIONS', CORS_LAMBDA_PATH, req.headers );
        res.send( '' );
    });

app.post(
    CORS_LAMBDA_PATH,
    bodyParser.text({ type: '*/*' }),
    ( req, res ) => {
        console.log( 'POST', CORS_LAMBDA_PATH, req.headers, req.body );
        corsLambda.handler( req, null, ( err, result ) => {
            console.log( 'RESULT', err, result );
            if( err ) {
                res.status( 501 ).send( err );
            } else {
                res.writeHead( result.statusCode, result.headers )
                    .end( result.body );
            }
        });
    });

app.options(
    MOCK_LAMBDA_PATH,
    cors(),
    ( req, res ) => {
        console.log( 'OPTIONS', MOCK_LAMBDA_PATH, req.headers );
        res.send( '' );
    });

app.post(
    MOCK_LAMBDA_PATH,
    bodyParser.text({ type: '*/*' }),
    ( req, res ) => {
        console.log( 'POST', MOCK_LAMBDA_PATH, req.headers, req.body );
        mockShopLambda.handler( req, null, ( err, result ) => {
            console.log( 'RESULT', err, result );
            if( err ) {
                res.status( 501 ).send( err );
            } else {
                res.writeHead( result.statusCode, result.headers )
                    .end( result.body );
            }
        });
    });


app.listen( PORT, () =>{
    console.log(
        'Server launched at:',
        `http://localhost:${ PORT }`,
    );
});
