import CleanWebpackPlugin   from 'clean-webpack-plugin';
import DotenvPlugin         from 'webpack-dotenv-extended-plugin';
import HtmlWebpackPlugin    from 'html-webpack-plugin';
import MiniCssExtractPlugin from 'mini-css-extract-plugin';

import {
    DIST,
    ROOT,
}   from './constants';


export default ({
    mode =              'development',
    appendPlugins =     [],
    ...overrides
}) => ({
    entry:              './packages/app/start-in-browser.js',
    mode,
    module: {
        rules: [{
            test:       /\.jsx?$/,
            exclude:    /node_modules/,
            loader:     'babel-loader',
        }, {
            test:       /\.s(a|c)ss$/,
            use: [
                ( mode === 'production'
                    ? MiniCssExtractPlugin.loader
                    : 'style-loader' // creates style nodes from JS strings
                ),
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
        new CleanWebpackPlugin(),
        new HtmlWebpackPlugin({
            title:      'Accelerated Text',
        }),
        ...( mode === 'production'
            ? [ new MiniCssExtractPlugin() ]
            : []
        ),
        ...appendPlugins,
    ],
    resolve: {
        alias: {
            react:          'preact/compat',
            'react-dom':    'preact/compat',
        },
    },
    output: {
        filename:       '[name].bundle.js',
        path:           DIST,
    },
    ...overrides,
});
