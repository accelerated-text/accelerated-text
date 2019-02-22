import { h, Component } from 'preact';

import SelectDataSample from '../data-samples/Select';
import { useStores }    from '../vesa/';


export default useStores([
    'planEditor',
])( class UploadDataSample extends Component {

    onChange = dataSampleId =>
        this.props.E.planEditor.onChangeDataSample

    render({
        E,
        planEditor: { dataSampleId },
    }) {
        return (
            <SelectDataSample
                onChange={ E.planEditor.onChangeDataSample }
                value={ dataSampleId }
            />
        );
    }
});
