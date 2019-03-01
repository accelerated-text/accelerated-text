
const CORS_HEADERS = {
    'access-control-allow-methods': 'GET, POST, PUT, DELETE, OPTIONS',
    'access-control-allow-origin':  '*',
};

module.exports = fn => ( method, url, body, status, headers ) =>
    fn(
        method,
        `${ process.env.NLG_API_URL }${ url }`,
        body,
        status,
        { ...CORS_HEADERS, ...headers }
    );
