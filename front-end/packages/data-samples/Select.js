import { h, Component }     from 'preact';

import { composeQueries }   from '../graphql/';
import {
    Info,
    Loading,
}                           from '../ui-messages/';
import { listDataFiles }    from '../graphql/queries.graphql';


export default composeQueries({
    listDataFiles,
})( class SelectDataSample extends Component {

    onChange = e =>
        this.props.onChange( e.target.value );

    render({
        className,
        listDataFiles: {
            error,
            listDataFiles,
            loading,
        },
        value,
    }) {
        return (
            loading
                ? <Loading message="Loading files" />
            : ( ! listDataFiles || ! listDataFiles.dataFiles )
                ? <Info message="No files" />
                : <select
                    className={ className }
                    onChange={ this.onChange }
                    value={ value }
                >
                    <option value="">select a file</option>
                    { listDataFiles.dataFiles.map(({ id, fileName }) =>
                        <option
                            children={ fileName }
                            key={ id }
                            value={ id }
                        />
                    )}
                </select>
        );
    }
});
