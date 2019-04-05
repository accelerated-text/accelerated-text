import unfetch          from 'isomorphic-unfetch';


export const getList = async () => {

    const response =    await unfetch( '/example-contexts.json' );
    return await response.json();
};
