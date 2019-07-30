import classnames           from 'classnames';
import {
    h,
    Component,
    createRef,
}                           from 'preact';
import PropTypes            from 'prop-types';

import { onConfirmDelete }  from '../ui-messages/';

import S                    from './InlineEditor.sass';


export default class InlineEditor extends Component {

    static propTypes = {
        cancelClassName:    PropTypes.string,
        className:          PropTypes.string,
        compact:            PropTypes.bool,
        inputClassName:     PropTypes.string,
        onDelete:           PropTypes.func,
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

    onClickDelete = evt => {
        evt.preventDefault();
        evt.stopPropagation();

        onConfirmDelete( this.props.onDelete );
    };

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

    onSubmit = evt => {
        evt.preventDefault();

        this.setState({
            isEditing:      false,
        });
        this.props.onSubmit( this.state.editText );
    };

    render({
        cancelClassName,
        children,
        className,
        compact,
        deleteClassName,
        inputClassName,
        onDelete,
        saveClassName,
        text,
        textClassName,
    }, {
        editText,
        isEditing,
    }) {
        return (
            <form
                className={ classnames(
                    S.className,
                    className,
                    compact && S.compact,
                    isEditing && S.isEditing,
                    onDelete && S.hasDelete,
                ) }
                onSubmit={ this.onSubmit }
            >
                { isEditing
                    ? [
                        <input
                            className={ inputClassName }
                            onKeyDown={ this.onKeyDown }
                            ref={ this.inputRef }
                            value={ editText }
                        />,
                        <button
                            children={ compact ? '✔️' : '✔️ Save' }
                            className={ saveClassName }
                            type="submit"
                        />,
                        <button
                            children={ compact ? '✖️' : '✖️ Cancel' }
                            className={ cancelClassName }
                            onClick={ this.onClickCancel }
                            type="reset"
                        />,
                    ]
                    : [
                        <div
                            children={ [ text, children ] }
                            className={ classnames( S.text, textClassName ) }
                            onClick={ this.onClickText }
                        />,
                        onDelete &&
                            <button
                                children="🗑️"
                                className={ classnames( S.delete, deleteClassName ) }
                                onClick={ this.onClickDelete }
                            />,
                    ]
                }
            </form>
        );
    }
}
