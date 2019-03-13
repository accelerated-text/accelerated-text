import unfetch          from 'isomorphic-unfetch';


export default async fileFromInput => {

    const fData =       new FormData;

    fData.append( 'key',    fileFromInput.name );
    fData.append( 'file',   fileFromInput );

    const URL =         `${ process.env.DATA_FILES_BUCKET }/`;
    const response =    await unfetch( URL, {
        method: 'POST',
        body:   fData,
        mode:   'cors',
    });

    if( response.status >= 400 ) {
        throw Error( response.statusText );
    } else {
        const contentType = response.headers.get( 'Content-Type' );

        if( contentType === 'application/json' ) {
            return await response.json();
        } else {
            return await response.blob();
        }
    }
};
