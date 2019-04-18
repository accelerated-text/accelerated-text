import { h, Component }     from 'preact';


export default class RowSelector extends Component {

    onChangeSelect = evt =>
        this.props.onChange( evt.target.value );

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
