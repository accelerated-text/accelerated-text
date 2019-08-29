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
