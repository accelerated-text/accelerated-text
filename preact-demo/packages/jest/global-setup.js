/// Imports --------------------------------------------------------------------

const jestPuppeteerSetup =  require( 'jest-environment-puppeteer/setup' );

const devServer =           require( './webpack-dev-server' );

/// Exports --------------------------------------------------------------------

module.exports = globalConfig => Promise.all([
    devServer,
    jestPuppeteerSetup( globalConfig ),
]);
