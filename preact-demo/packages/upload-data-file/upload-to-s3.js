import unfetch          from 'isomorphic-unfetch';


const URL =             `${ process.env.DATA_FILES_BUCKET }/`;


export default async ( key, file ) => {

    const fData =       new FormData;

    fData.append( 'key',    key );
    fData.append( 'file',   file );

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
