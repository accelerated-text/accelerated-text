import classnames           from 'classnames';
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
        className,
        fileClassName,
        uploadClassName,
        uploadDataFile: {
            uploadCounter,
            uploadError,
            uploadFileKey,
            uploadLoading,
        },
    }) {
        return (
            <form
                className={ classnames( S.className, className ) }
                onSubmit={ this.onSubmit }
            >
                <input
                    className={ classnames( S.file, fileClassName ) }
                    disabled={ uploadLoading }
                    key={ uploadCounter }
                    type="file"
                />
                <button
                    children={ uploadLoading ? 'Uploading...' : 'Upload' }
                    className={ classnames( S.upload, uploadClassName ) }
                    disabled={ uploadLoading }
                    type="submit"
                />
                {
                    uploadError
                        ? <Error justIcon message={ uploadError } />
                    : uploadLoading
                        ? <Loading justIcon message="Uploading..." />
                    : uploadFileKey
                        ? <Loading justIcon message="Syncing..." />
                    : uploadCounter
                        ? <Success message="Done" />
                        : null
                }
            </form>
        );
    }
}));
