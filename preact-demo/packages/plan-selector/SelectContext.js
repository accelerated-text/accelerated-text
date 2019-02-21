import { h, Component } from 'preact';

import { Loading }      from '../ui-messages/';
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
        } else {
            return (
                <select
                    onChange={ this.onChange }
                    value={ value }
                >
                    <option value="">select a context</option>
                    { contexts.map(({ id, name }) =>
                        <option key={ id } name={ id }>{ name }</option>
                    )}
                </select>
            );
        }
    }
});
