import classnames           from 'classnames';
import { h, Component }     from 'preact';

import { Info, Loading }    from '../ui-messages/';
import { QA }               from '../tests/constants';
import SelectDataSample     from '../document-plans/SelectDataSample';
import UploadDataFile       from '../upload-data-file/UploadDataFile';
import { useStores }        from '../vesa/';

import Download             from './Download';
import S                    from './Files.sass';


export default useStores([
    'dataSamples',
])( class DataManagerFiles extends Component {

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
        dataSamples: {
            getListError,
            getListLoading,
        },
        plan,
    }, {
        uploadOpen,
    }) {
        return (
            <div className={ classnames( S.className, className ) }>
                <div className={ S.main }>{
                    getListLoading
                        ? <Loading message="Loading file list" />
                    : uploadOpen
                        ? <UploadDataFile
                            fileClassName={ QA.DATA_MANAGER_FILE_BROWSE }
                            uploadClassName={ QA.DATA_MANAGER_FILE_UPLOAD }
                            onUploadDone={ this.onUploadDone }
                        />
                    : plan
                        ? [
                            <SelectDataSample
                                className={ classnames( S.selectFile, QA.DATA_MANAGER_FILE_LIST ) }
                                plan={ plan }
                            />,
                            <Download
                                className={ classnames( S.downloadFile, QA.DATA_MANAGER_FILE_DOWNLOAD ) }
                                plan={ plan }
                            />,
                        ]
                        : <Info message="Please open a document plan to see data files." />
                }</div>
                <div className={ S.right }>{
                    getListLoading
                        ? null
                    : uploadOpen
                        ? <button
                            children="✖️"
                            className={ classnames( S.close, QA.DATA_MANAGER_FILE_CLOSE ) }
                            onClick={ this.onClickClose }
                        />
                    : plan
                        ? <button
                            children="➕ Add"
                            className={ classnames( S.add, QA.DATA_MANAGER_FILE_ADD ) }
                            onClick={ this.onClickAdd }
                        />
                        : null
                }</div>
            </div>
        );
    }
});
