import classnames           from 'classnames';
import { h, Component }     from 'preact';

import S                    from './FlagValue.sass';


export default class ReaderFlagValue extends Component {
    
    onChange = () =>
        this.props.onChange( this.props.flag.id );
        
    render({
        className,
        flag,
        flagValues,
        onChange,
    }) {
        const isChecked =   flagValues[flag.id];

        return (
            <label className={ classnames( S.className, className, isChecked && S.isChecked ) }>
                <input
                    checked={ isChecked }
                    onChange={ this.onChange }
                    type="checkbox"
                />
                { flag.name }
            </label>
        );
    }
}
