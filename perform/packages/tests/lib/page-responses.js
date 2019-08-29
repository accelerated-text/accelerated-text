import { createDataFileData }   from '../data/data-file-data';
import USER                     from '../data/user';


export default ({ graphqlApi, nlgApi }, responses ) => {

    const dataFile = (
        responses.dataFiles
        && responses.dataFiles[0]
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
        nlgApi.provideOnce( 'GET', `/data/?user=${ USER.id }`, responses.dataFiles ),
        nlgApi.provideOnce( 'GET', '/document-plans/', responses.documentPlans )
            .then(() => Promise.all([
                dataFile &&
                    nlgApi.provideOnce( 'GET', `/data/${ dataFile.key }`, createDataFileData({
                        fieldNames: dataFile.fieldNames,
                    })),
                planHasDataFile &&
                    nlgApi.provideOnce( 'OPTIONS', '/nlg/', '' )
                        .then(() => nlgApi.provideOnce( 'POST', '/nlg/', responses.nlgJob ))
                        .then(() => nlgApi.provideOnce( 'GET', `/nlg/${ responses.nlgJob.resultId }`, responses.nlgJobResult )),
            ])),
    ]);
};
