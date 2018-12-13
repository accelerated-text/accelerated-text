import fetch            from 'isomorphic-unfetch';


const URL =             'https://zihxo6d93h.execute-api.eu-central-1.amazonaws.com/Prod/document-plans/test/variants';

export const getForDataSample = async () => {

    const response =    await fetch( URL, {
        method:         'GET',
        mode:           'no-cors',
    });
    return response.json();
};

export default {
    getForDataSample,
};
