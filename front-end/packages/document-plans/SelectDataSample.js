import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import SelectDataSample     from '../data-samples/Select';

import PlanActions          from './Actions';


export default PlanActions(
    class UploadDataSample extends Component {

        static propTypes = {
            className:      PropTypes.string,
            onUpdatePlan:   PropTypes.func.isRequired,
            plan:           PropTypes.object,
        };

        onChange = dataSampleId =>
            this.props.onUpdatePlan({
                ...this.props.plan,
                dataSampleId,
                dataSampleRow:  0,
                dataSampleMethod: 'relevant'
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
