const { host, ip, mountPath } = require( './constants' );

module.exports = {
    [mountPath]: {
        target:                 `http://${ ip }`,
        headers: {
            host,
        },
        pathRewrite: {
            [`^${ mountPath }`]:    '',
        },
    },
};
