import { h, Component } from 'preact';

import {
    Info,
    Loading,
}   from '../ui-messages/';
import { useStores }    from '../vesa/';


export default useStores([
    'dataSamples',
])( class SelectDataSample extends Component {

    onChange = e =>
        this.props.onChange( e.target.value );

    render({
        className,
        dataSamples: {
            fileIds,
            fileItems,
            getListError,
            getListLoading,
        },
        value,
    }) {
        if( getListLoading ) {
            return <Loading message="Loading files" />;
        } else if( !fileIds || !fileIds.length ) {
            return <Info message="No files" />;
        } else {
            return (
                <select
                    className={ className }
                    onChange={ this.onChange }
                    value={ value }
                >
                    <option value="">select a file</option>
                    { fileIds.map( id =>
                        <option key={ id } value={ id }>{ fileItems[id].fileName }</option>
                    )}
                </select>
            );
        }
    }
});
