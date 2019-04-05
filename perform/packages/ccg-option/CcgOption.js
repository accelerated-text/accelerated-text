import { h, Component }     from 'preact';


export default class CcgOption extends Component {

    onChange = evt =>
        this.props.onChange({
            ...this.props.plan,
            useCcg:         evt.target.checked,
        });

    render({ plan, onChange }) {
        return (
            <label>
                <input
                    checked={ plan && plan.useCcg }
                    disabled={ !plan }
                    onChange={ this.onChange }
                    type="checkbox"
                />
                Use CCG
            </label>
        );
    }
}
