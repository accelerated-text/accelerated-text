export default ({ id, files }) =>
    files.find( file => file.id === id );
