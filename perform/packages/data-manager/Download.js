import { h, Component }     from 'preact';

import DocumentPlansContext from '../document-plans/Context';
import getFileUrl           from '../upload-data-file/get-file-url';
import { useStores }        from '../vesa/';


export default useStores([
    'user',
])( class DataManagerDownload extends Component {

    static contextType =    DocumentPlansContext;

    render({ className, user }, _, { openedDataFile }) {
        return (
            ( ! openedDataFile )
                ? null
                : <a
                    children="Download file"
                    className={ className }
                    href={ getFileUrl( user, openedDataFile.fileName ) }
                />
        );
    }
});
