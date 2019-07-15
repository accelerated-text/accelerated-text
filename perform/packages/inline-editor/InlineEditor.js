import classnames           from 'classnames';
import {
    h,
    Component,
    createRef,
}                           from 'preact';
import PropTypes            from 'prop-types';

import S                    from './InlineEditor.sass';


export default class InlineEditor extends Component {

    static propTypes = {
        cancelClassName:    PropTypes.string,
        className:          PropTypes.string,
        inputClassName:     PropTypes.string,
        onSubmit:           PropTypes.func.isRequired,
        saveClassName:      PropTypes.string,
        text:               PropTypes.string.isRequired,
        textClassName:      PropTypes.string,
    };

    inputRef =              createRef();

    state = {
        editText:           '',
        isEditing:          false,
    };

    onClickCancel = () =>
        this.setState({
            isEditing:      false,
        });

    onClickText = () => {
        this.setState({
            editText:       this.props.text,
            isEditing:      true,
        }, () => {
            this.inputRef.current.focus();
        });
    };

    onKeyDown = evt =>
        this.setState(
            ( evt.key === 'Escape' )
                ? {
                    isEditing:  false,
                }
                : {
                    editText:   evt.target.value,
                }
        );

    onSubmit = () => {
        this.setState({
            isEditing:      false,
        });
        this.props.onSubmit( this.state.editText );
    };

    render({
        cancelClassName,
        className,
        inputClassName,
        saveClassName,
        text,
        textClassName,
    }, {
        editText,
        isEditing,
    }) {
        return (
            <div className={ classnames( S.className, className ) }>{
                isEditing
                    ? <form onSubmit={ this.onSubmit }>
                        <input
                            className={ inputClassName }
                            onKeyDown={ this.onKeyDown }
                            ref={ this.inputRef }
                            value={ editText }
                        />
                        <button
                            children="✔️ Save"
                            className={ saveClassName }
                            type="submit"
                        />
                        <button
                            children="✖️ Cancel"
                            className={ cancelClassName }
                            onClick={ this.onClickCancel }
                            type="reset"
                        />
                    </form>
                    : <div
                        children={ text }
                        className={ classnames( S.text, textClassName ) }
                        onClick={ this.onClickText }
                    />
            }</div>
        );
    }
}
