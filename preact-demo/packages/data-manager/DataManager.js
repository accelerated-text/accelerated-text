import { h, Component }     from 'preact';

import SelectDataSample     from '../document-plans/SelectDataSample';
import UploadDataFile       from '../upload-data-file/UploadDataFile';
import { useStores }        from '../vesa/';

import getPlanFile          from './get-plan-file';
import S                    from './DataManager.sass';


export default useStores([
    'dataSamples',
])( class DataManager extends Component {

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
        const planFile =        getPlanFile( files, plan );

        return (
            <div className={ S.className }>
                <div className={ S.files }>
                    <SelectDataSample plan={ plan } />
                    <div className={ uploadOpen ? S.upload : '' }>{
                        uploadOpen
                            ? [
                                <UploadDataFile />,
                                <button className={ S.close } onClick={ this.onClickClose }>✖️</button>,
                            ]
                            : <button className={ S.add } onClick={ this.onClickAdd }>➕ Add</button>
                    }</div>
                </div>
                { planFile && planFile.fieldNames &&
                    <ul>{ planFile.fieldNames.map( name =>
                        <li>{ name }</li>
                    )}</ul>
                }
            </div>
        );
    }
});
