import classnames           from 'classnames';
import { h, Component }     from 'preact';

export default class SampleAlgorithm extends Component {
    onChange = e =>
        this.props.onChange( e.target.value );

    render({value}) {
        return (
            <div onChange={ this.onChange }>
                <input type="radio" name="select-method" value="relevant" /> Most relevant
                <input type="radio" name="select-method" value="first" /> First 20
            </div>
        );
    }
}