const responseMocker =      require( '../qa-utils/response-mocker' );


const CORS_HEADERS = {
    'access-control-allow-methods': 'GET, POST, PUT, DELETE, OPTIONS',
    'access-control-allow-origin':  '*',
};


module.exports = page => {

    const mocker = responseMocker( page, process.env.NLG_API_URL );

    const mockResponse =
        ( method, path, bodyData, status = 200, headers = {}) =>
            mocker.mockResponse( method, path, bodyData, status, {
                ...CORS_HEADERS,
                ...headers,
            });

    return {
        ...mocker,
        mockResponse,
    };
};
