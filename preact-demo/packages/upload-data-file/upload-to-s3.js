const BUCKET_URL =  'https://s3.eu-central-1.amazonaws.com/augmented-writer-data-files';


export default fileFromInput => {

    const fData =   new FormData;

    fData.append( 'key',    fileFromInput.name );
    fData.append( 'file',   fileFromInput );

    return fetch( `${ BUCKET_URL }/`, {
        method:     'POST',
        body:       fData,
        mode:       'cors',
    });
};
