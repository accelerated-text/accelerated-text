const jestPuppeteerTeardown =   require( 'jest-environment-puppeteer/teardown' );

const devServer =               require( './webpack-dev-server' );


module.exports = globalConfig => Promise.all([
    devServer.then(
        server => server.close()
    ),
    jestPuppeteerTeardown( globalConfig ),
]);
