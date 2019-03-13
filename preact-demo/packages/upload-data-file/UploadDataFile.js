import { h, Component }     from 'preact';

import { mount, useStores } from '../vesa/';

import adapter              from './adapter';
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
            uploadError,
            uploadLoading,
        },
    }) {
        return (
            <form onSubmit={ this.onSubmit }>
                { uploadError && 'Error!' }
                { uploadLoading && 'Loading!' }
                <input name="file" type="file" />
                <button
                    disabled={ uploadLoading }
                    children="Upload"
                    type="submit"
                />
            </form>
        );
    }
}));
