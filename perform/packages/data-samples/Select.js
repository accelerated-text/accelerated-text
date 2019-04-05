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
        dataSamples: {
            files,
            getListError,
            getListLoading,
        },
        value,
    }) {
        if( getListLoading ) {
            return <Loading message="Loading files" />;
        } else if( !files || !files.length ) {
            return <Info message="No files" />;
        } else {
            return (
                <select
                    onChange={ this.onChange }
                    value={ value }
                >
                    <option value="">select a file</option>
                    { files.map(({ id, fileName }) =>
                        <option key={ id } value={ id }>{ fileName }</option>
                    )}
                </select>
            );
        }
    }
});
