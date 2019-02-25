import { h, Component } from 'preact';

import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { useStores }    from '../vesa/';


export default useStores([
    'contexts',
])( class SelectContext extends Component {

    onChange = e =>
        this.props.onChange( e.target.value );

    render({
        contexts: {
            contexts,
            getListError,
            getListLoading,
        },
        value,
    }) {
        if( getListLoading ) {
            return <Loading message="Loading contexts" />;
        } else if( !contexts ) {
            if( getListError ) {
                return <Error message="Error loading contexts" />;
            } else {
                return <Info message="No contexts yet." />;
            }
        } else {
            return (
                <select
                    onChange={ this.onChange }
                    value={ value }
                >
                    <option value="">select a context</option>
                    { contexts.map(({ id, name }) =>
                        <option key={ id } value={ id }>{ name }</option>
                    )}
                </select>
            );
        }
    }
});
