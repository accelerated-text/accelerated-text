const webpack =             require( 'webpack' );
const WebpackDevServer =    require( 'webpack-dev-server' );

const webpackConfig =       require( '../webpack/config' ).default;


const OPTIONS = {
    ...webpackConfig.devServer,
    host:                   process.env.TEST_HOST,
    port:                   process.env.TEST_PORT,
    stats:                  'none',
};

/// Main

const compiler =        webpack( webpackConfig );
const server =          new WebpackDevServer( compiler, OPTIONS );

/// Exports

module.exports = new Promise(( resolve, reject ) => {

    server.listen(
        OPTIONS.port,
        OPTIONS.host,
        err => err ? reject( err ) : resolve( server )
    );
});
