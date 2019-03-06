import webpack              from 'webpack';

import analysisProxy        from '../analysis-api/http-proxy-middleware-config';

import { ASSETS, DIST }     from './constants';
import config               from './config';


export default config({
    mode:               'development',
    appendPlugins: [
        new webpack.HotModuleReplacementPlugin(),
    ],
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
});

