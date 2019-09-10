import { h, Component }     from 'preact';

import getFileUrl           from '../upload-data-file/get-file-url';
import OpenedFileContext    from '../accelerated-text/OpenedDataFileContext';


export default class DataManagerDownload extends Component {

    static contextType =    OpenedFileContext;

    render({ className, user }, _, { file }) {
        return (
            ( ! file )
                ? null
                : <a
                    children="Download file"
                    className={ className }
                    href={ getFileUrl( file.fileName ) }
                />
        );
    }
}
