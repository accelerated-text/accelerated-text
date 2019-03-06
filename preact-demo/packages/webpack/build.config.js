import MiniCssExtractPlugin from 'mini-css-extract-plugin';

import config               from './config';


export default config({
    mode:               'production',
    appendPlugins: [
        new MiniCssExtractPlugin(),
    ],
    devtool:            'source-map',
});
