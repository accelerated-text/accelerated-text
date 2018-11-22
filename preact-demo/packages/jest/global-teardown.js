/// Imports --------------------------------------------------------------------

const jestPuppeteerTeardown =   require( 'jest-environment-puppeteer/teardown' );

const devServer =               require( './webpack-dev-server' );

/// Exports --------------------------------------------------------------------

module.exports = globalConfig => Promise.all([
    devServer.then(
        server => server.close()
    ),
    jestPuppeteerTeardown( globalConfig ),
]);
