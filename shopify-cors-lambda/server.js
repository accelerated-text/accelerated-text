/// Imports --------------------------------------------------------------------

require( 'dotenv-extended/config' );

const bodyParser =          require( 'body-parser' );
const cors =                require( 'cors' );
const express =             require( 'express' );

const { handler } =         require( './index' );


/// Constants ------------------------------------------------------------------

const PATH =                '/';
const PORT =                process.env.LOCAL_PORT || 8090;

/// Main -----------------------------------------------------------------------

const app =                 express();

app.options(
    PATH,
    cors(),
    ( req, res ) => {
        console.log( 'OPTIONS', PATH, req.headers );
        res.send( '' );
    });

app.post(
    PATH,
    bodyParser.text({ type: '*/*' }),
    ( req, res ) => {
        console.log( 'POST', PATH, req.headers, req.body );
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

app.listen( PORT, () =>{
    console.log(
        'Server launched at:',
        `http://localhost:${ PORT }`,
    );
});
