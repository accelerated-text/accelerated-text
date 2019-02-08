const CleanWebpackPlugin =  require( 'clean-webpack-plugin' );
const DotenvPlugin =        require( 'webpack-dotenv-extended-plugin' );
const HtmlWebpackPlugin =   require( 'html-webpack-plugin' );
const path =                require( 'path' );
const webpack =             require( 'webpack' );

const analysisProxy =       require( '../analysis-api/http-proxy-middleware-config' );


const ASSETS =              path.resolve( __dirname, '../../assets' );
const DIST =                path.resolve( __dirname, 'dist' );
const ROOT =                path.resolve( __dirname, '../..' );


module.exports = {
    devtool:            'inline-source-map',
    devServer: {
        contentBase:    [
            DIST,
            ASSETS,
        ],
        hot:            true,
        open:           false,
        proxy: {
            ...analysisProxy,
        },
    },
    entry:              './packages/app/start-in-browser.js',
    mode:               'development',
    module: {
        rules: [{
            test:       /\.jsx?$/,
            exclude:    /node_modules/,
            loader:     'babel-loader',
        }, {
            test:       /\.s(a|c)ss$/,
            use: [
                'style-loader', // creates style nodes from JS strings
                'css-loader?modules=true',   // translates CSS into CommonJS
                'sass-loader',  // compiles Sass to CSS
            ],
        }, {
            test:       /\.xml$/,
            loader:     'raw-loader',
        }],
    },
    plugins: [
        new DotenvPlugin({
            defaults:   process.env.dotenv_config_defaults || `${ ROOT }/.env.defaults`,
            path:       process.env.dotenv_config_path || `${ ROOT }/.env`,
        }),
        new CleanWebpackPlugin([ DIST ]),
        new HtmlWebpackPlugin({
            title:      'Augmented Writer',
        }),
        new webpack.HotModuleReplacementPlugin(),
    ],
    output: {
        filename:       '[name].bundle.js',
        path:           DIST,
    },
};
