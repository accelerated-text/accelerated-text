require( 'dotenv-extended/config' );
const jestPuppeteerSetup =  require( 'jest-environment-puppeteer/setup' );

const devServer =           require( './webpack-dev-server' );


module.exports = globalConfig => Promise.all([
    devServer,
    jestPuppeteerSetup( globalConfig ),
]);
