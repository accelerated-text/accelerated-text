import { h, Component }     from 'preact';

import { Loading }          from '../ui-messages/';
import SelectDataSample     from '../document-plans/SelectDataSample';
import UploadDataFile       from '../upload-data-file/UploadDataFile';
import { useStores }        from '../vesa/';

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
        dataSamples: {
            files,
            getListError,
            getListLoading,
        },
        plan,
    }) {
        const { uploadOpen } =  this.state;

        return (
            <div className={ S.className }>
                <div className={ S.main }>{
                    getListLoading
                        ? <Loading message="Loading file list" />
                    : uploadOpen
                        ? <UploadDataFile />
                        : <SelectDataSample plan={ plan } />
                }</div>
                <div className={ S.right }>{
                    getListLoading
                        ? null
                    : uploadOpen
                        ? <button className={ S.close } onClick={ this.onClickClose }>✖️</button>
                        : <button className={ S.add } onClick={ this.onClickAdd }>➕ Add</button>
                }</div>
            </div>
        );
    }
});
