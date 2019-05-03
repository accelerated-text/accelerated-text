export default ( user, fileName ) =>
    `${ process.env.DATA_FILES_BUCKET }/${ user.id }/${ fileName }`;
