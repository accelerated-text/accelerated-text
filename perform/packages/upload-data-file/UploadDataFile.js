import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import {
    Error,
    Loading,
    Success,
}   from '../ui-messages/';

import S                    from './UploadDataFile.sass';
import withUploader         from './withUploader';


export default withUploader( class UploadDataFile extends Component {

    static propTypes = {
        className:          PropTypes.string,
        fileClassName:      PropTypes.string,
        onUpload:           PropTypes.func.isRequired,
        uploadClassName:    PropTypes.string,
        uploader:           PropTypes.object.isRequired,
    };

    onSubmit = evt => {
        evt.preventDefault();

        const form =        evt.target;
        const file =        form[0].files[0];

        if( file ) {
            this.props.onUpload( file );
        }
    }

    render({
        className,
        fileClassName,
        uploadClassName,
        uploader: {
            counter,
            error,
            fileKey,
            loading,
        },
    }) {
        return (
            <form
                className={ classnames( S.className, className ) }
                onSubmit={ this.onSubmit }
            >
                <input
                    className={ classnames( S.file, fileClassName ) }
                    disabled={ loading }
                    key={ counter }
                    type="file"
                />
                <button
                    children={ loading ? 'Uploading...' : 'Upload' }
                    className={ classnames( S.upload, uploadClassName ) }
                    disabled={ loading }
                    type="submit"
                />
                {
                    error
                        ? <Error justIcon message={ error } />
                    : loading
                        ? <Loading justIcon message="Uploading..." />
                    : fileKey
                        ? <Loading justIcon message="Syncing..." />
                    : counter
                        ? <Success message="Done" />
                        : null
                }
            </form>
        );
    }
});
