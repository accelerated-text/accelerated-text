import { POST }         from './fetch';


export default async text =>
    POST( '/analyser/combined', {
        lang:           'en',
        text,
    }).then(
        response =>     response.results
    );
