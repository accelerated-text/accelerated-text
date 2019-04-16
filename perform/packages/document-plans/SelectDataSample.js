import { h, Component } from 'preact';

import SelectDataSample from '../data-samples/Select';
import { useStores }    from '../vesa/';


export default useStores([])(
    class UploadDataSample extends Component {

        onChange = dataSampleId =>
            this.props.E.documentPlans.onUpdate({
                ...this.props.plan,
                dataSampleId,
            });

        render({ className, plan }) {
            return (
                <SelectDataSample
                    className={ className }
                    onChange={ this.onChange }
                    value={ plan && plan.dataSampleId }
                />
            );
        }
    }
);
