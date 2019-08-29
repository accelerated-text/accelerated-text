import { path }             from 'ramda';


export default ({ graphqlApi, nlgApi }, responses ) => {

    const dataFile = path(
        [ 'dataFiles', 'listDataFiles', 'dataFiles', 0 ],
        responses,
    );

    const planHasDataFile = (
        responses.documentPlans
        && responses.documentPlans[0]
        && responses.documentPlans[0].dataSampleId
    );

    return Promise.all([
        graphqlApi.provideOnce( 'concepts', {}, { data: responses.concepts }),
        graphqlApi.provideOnce( 'dictionary', {}, { data: responses.dictionary }),
        graphqlApi.provideOnce( 'readerFlags', {}, { data: responses.readerFlags }),
        graphqlApi.provideOnce( 'listDataFiles', {}, { data: responses.dataFiles }),
        nlgApi.provideOnce( 'GET', '/document-plans/', responses.documentPlans )
            .then(() => Promise.all([
                dataFile &&
                    graphqlApi.provideOnce(
                        'getDataFile',
                        { id: dataFile.id },
                        { data: { getDataFile: dataFile }},
                    ),
                planHasDataFile &&
                    nlgApi.provideOnce( 'OPTIONS', '/nlg/', '' )
                        .then(() => nlgApi.provideOnce( 'POST', '/nlg/', responses.nlgJob ))
                        .then(() => nlgApi.provideOnce( 'GET', `/nlg/${ responses.nlgJob.resultId }`, responses.nlgJobResult )),
            ])),
    ]);
};
