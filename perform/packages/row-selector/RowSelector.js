import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';


export default class RowSelector extends Component {

    static propTypes = {
        className:          PropTypes.string,
        onChange:           PropTypes.func.required,
        rows:               PropTypes.array.required,
        selected:           PropTypes.number,
    };

    onChangeSelect = evt =>
        this.props.onChange( parseInt( evt.target.value, 10 ));

    onClickNext = () =>
        this.props.onChange( this.props.selected + 1 );

    onClickPrevious = () =>
        this.props.onChange( this.props.selected - 1 );
    
    render({ className, onChange, rows, selected }) {
        return (
            <div className={ className }>
                <button
                    children="◀️"
                    disabled={ !selected }
                    onClick={ this.onClickPrevious }
                />
                <select
                    disabled={ rows.length < 2 }
                    onChange={ this.onChangeSelect }
                    value={ selected }
                >
                    { rows.map(( row, i ) =>
                        <option children={ i + 1 } value={ i } />
                    )}
                </select>
                <button
                    children="▶️"
                    disabled={ selected >= rows.length - 1 }
                    onClick={ this.onClickNext }
                />
            </div>
        );
    }
}
