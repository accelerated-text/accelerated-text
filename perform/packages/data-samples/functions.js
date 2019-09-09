export const dataFieldsToObj = ( record = []) =>
    Object.fromEntries(
        record.map( field => [ field.fieldName, field.value ])
    );

export const getPlanDataRow = ( dataFile, plan ) => (
    dataFile
    && dataFile.records
    && plan
    && plan.dataSampleId
    && dataFieldsToObj( dataFile.records[plan.dataSampleRow].fields )
);


export const getPlanDataRecord = ( dataFile, plan ) => (
    dataFile
    && dataFile.records
    && plan
    && plan.dataSampleId
    && dataFile.records[plan.dataSampleRow]
);


export const getFileById = ( listDataFiles, id ) => (
    id
    && listDataFiles
    && listDataFiles.dataFiles
    && listDataFiles.dataFiles.find(
        file => file.id === id
    )
);
