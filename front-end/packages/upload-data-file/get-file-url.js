import USER                 from '../user/';


export default fileName =>
    `${ process.env.DATA_FILES_BUCKET }/${ USER.id }/${ fileName }`;
