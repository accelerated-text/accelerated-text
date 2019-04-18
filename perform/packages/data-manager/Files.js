import classnames           from 'classnames';
import { h, Component }     from 'preact';

import { Loading }          from '../ui-messages/';
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

    render({
        className,
        dataSamples: {
            getListError,
            getListLoading,
        },
        plan,
    }) {
        const showUpload = (
            this.state.uploadOpen
            || !plan
        );

        return (
            <div className={ classnames( S.className, className ) }>
                <div className={ S.main }>{
                    getListLoading
                        ? <Loading message="Loading file list" />
                    : showUpload
                        ? <UploadDataFile />
                        : [
                            <SelectDataSample className={ S.selectFile } plan={ plan } />,
                            <Download className={ S.downloadFile } plan={ plan } />,
                        ]
                }</div>
                <div className={ S.right }>{
                    getListLoading
                        ? null
                    : showUpload
                        ? <button className={ S.close } onClick={ this.onClickClose }>✖️</button>
                        : <button className={ S.add } onClick={ this.onClickAdd }>➕ Add</button>
                }</div>
            </div>
        );
    }
});
