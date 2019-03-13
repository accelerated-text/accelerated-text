import { h, Component }     from 'preact';

import {
    Error,
    Loading,
    Success,
}   from '../ui-messages/';
import { mount, useStores } from '../vesa/';

import adapter              from './adapter';
import S                    from './UploadDataFile.sass';
import uploadDataFile       from './store';


export default mount({
    uploadDataFile,
}, [
    adapter,
])( useStores([
    'uploadDataFile',
])( class UploadDataFile extends Component {

    onSubmit = evt => {
        evt.preventDefault();

        const form =        evt.target;
        const file =        form[0].files[0];

        if( file ) {
            this.props.E.uploadDataFile.onUpload( file );
        }
    }

    render({
        uploadDataFile: {
            uploadCounter,
            uploadError,
            uploadLoading,
        },
    }) {
        return (
            <form className={ S.className } onSubmit={ this.onSubmit }>
                <input
                    className={ S.file }
                    disabled={ uploadLoading }
                    key={ uploadCounter }
                    type="file"
                />
                <button
                    children={ uploadLoading ? 'Uploading...' : 'Upload' }
                    className={ S.upload }
                    disabled={ uploadLoading }
                    type="submit"
                />
                {
                    uploadError
                        ? <Error justIcon message={ uploadError } />
                    : uploadLoading
                        ? <Loading justIcon message="Uploading..." />
                    : uploadCounter
                        ? <Success message="Done" />
                        : null
                }
            </form>
        );
    }
}));
