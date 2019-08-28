import classnames           from 'classnames';
import { h, Component }     from 'preact';

import { Loading }          from '../ui-messages/';
import { QA }               from '../tests/constants';
import SelectDataSample     from '../document-plans/SelectDataSample';
import UploadDataFile       from '../upload-data-file/UploadDataFile';

import Download             from './Download';
import S                    from './Files.sass';


export default class DataManagerFiles extends Component {

    state = {
        uploadOpen:         false,
    };

    onClickAdd = () =>
        this.setState({ uploadOpen: true });

    onClickClose = () =>
        this.setState({ uploadOpen: false });

    onUploadDone = () => {
        this.setState({ uploadOpen: false });
    };

    render({
        className,
        error,
        loading,
        plan,
    }) {
        const showUpload = (
            this.state.uploadOpen
            || !plan
        );

        return (
            <div className={ classnames( S.className, className ) }>
                <div className={ S.main }>{
                    loading
                        ? <Loading message="Loading file list" />
                    : showUpload
                        ? <UploadDataFile
                            fileClassName={ QA.DATA_MANAGER_FILE_BROWSE }
                            uploadClassName={ QA.DATA_MANAGER_FILE_UPLOAD }
                            onUploadDone={ this.onUploadDone }
                        />
                        : [
                            <SelectDataSample
                                className={ classnames( S.selectFile, QA.DATA_MANAGER_FILE_LIST ) }
                                plan={ plan }
                            />,
                            <Download
                                className={ classnames( S.downloadFile, QA.DATA_MANAGER_FILE_DOWNLOAD ) }
                                plan={ plan }
                            />,
                        ]
                }</div>
                <div className={ S.right }>{
                    loading
                        ? null
                    : showUpload
                        ? <button
                            children="✖️"
                            className={ classnames( S.close, QA.DATA_MANAGER_FILE_CLOSE ) }
                            onClick={ this.onClickClose }
                        />
                        : <button
                            children="➕ Add"
                            className={ classnames( S.add, QA.DATA_MANAGER_FILE_ADD ) }
                            onClick={ this.onClickAdd }
                        />
                }</div>
            </div>
        );
    }
}
