import { h, Component } from 'preact';

import SelectDataSample from '../data-samples/Select';
import { useStores }    from '../vesa/';


export default useStores([
    'planEditor',
])( class UploadDataSample extends Component {

    onChange = dataSampleId =>
        this.props.E.documentPlans.onUpdate({
            ...this.props.plan,
            dataSampleId,
        });

    render({ plan }) {
        return (
            <SelectDataSample
                onChange={ this.onChange }
                value={ plan && plan.dataSampleId }
            />
        );
    }
});
