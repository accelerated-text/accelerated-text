const webpack =           require( 'webpack' );
const webpackDevServer =  require( 'webpack-dev-server' );

const webpackConfig =     require( '../webpack/config' );


const OPTIONS = {
    host:               process.env.npm_package_config_test_host,
    port:               process.env.npm_package_config_test_port,
    stats:              'none',
}

/// Main

const compiler =        webpack( webpackConfig );
const server =          new webpackDevServer( compiler, OPTIONS );

/// Exports

module.exports = new Promise(( resolve, reject ) => {

  server.listen(
    OPTIONS.port,
    OPTIONS.host,
    err => err ? reject( err ) : resolve( server )
  );
});
