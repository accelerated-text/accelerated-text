import { path }             from 'ramda';

import {
    createDataFileFull,
}                           from '../data/data-file';


export default ({ log, graphqlApi, nlgApi }, responses ) => {

    const dataFile = path(
        [ 'dataFiles', 'listDataFiles', 'dataFiles', 0 ],
        responses,
    );

    const dataSampleId = path(
        [ 'documentPlans', 0, 'dataSampleId' ],
        responses,
    );

    return Promise.all([
        graphqlApi.provideOnce( 'concepts', {}, { data: responses.concepts }),
        graphqlApi.provideOnce( 'dictionary', {}, { data: responses.dictionary }),
        graphqlApi.provideOnce( 'readerFlags', {}, { data: responses.readerFlags }),
        graphqlApi.provideOnce( 'listDataFiles', {}, { data: responses.dataFiles }),
        nlgApi.provideOnce( 'GET', '/document-plans/', responses.documentPlans )
            .then(() => dataSampleId && Promise.all([
                graphqlApi.provideOnce(
                    'getDataFile',
                    { id: dataSampleId },
                    { data: (
                        dataFile
                            ? { getDataFile: createDataFileFull( dataFile ) }
                            : { getDataFile: null,
                                errors: [{
                                    message:    `No data file found for ${ dataSampleId }`,
                                }],
                            }
                    ) },
                ),
                nlgApi.provideOnce( 'OPTIONS', '/nlg/', '' )
                    .then(() => nlgApi.provideOnce( 'POST', '/nlg/', responses.nlgJob ))
                    .then(() => nlgApi.provideOnce( 'GET', `/nlg/${ responses.nlgJob.resultId }`, responses.nlgJobResult )),
            ])),
    ]);
};
