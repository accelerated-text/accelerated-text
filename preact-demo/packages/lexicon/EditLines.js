import classnames       from 'classnames';
import { h, Component } from 'preact';

import { QA }           from '../tests/constants';

import S                from './EditLines.sass';


export default class LexiconEditLines extends Component {

    state = {
        text:   this.props.lines.join( '\n' ),
    };

    onClickCancel = () => {
        this.props.onClickCancel();
    };

    onClickSave = () =>
        this.props.onClickSave( this.state.text.split( '\n' ));

    onInput = evt =>
        this.setState({
            text:       evt.target.value,
        });

    render({ saving, status }, { text }) {
        return (
            <div className={ classnames( S.className, QA.LEXICON_EDIT ) }>
                <textarea
                    className={ QA.LEXICON_EDIT_TEXT }
                    disabled={ saving }
                    onInput={ this.onInput }
                    value={ text }
                />
                <button
                    children="Save"
                    className={ QA.LEXICON_EDIT_SAVE }
                    disabled={ saving }
                    onClick={ this.onClickSave }
                />
                <button
                    children="Cancel"
                    className={ QA.LEXICON_EDIT_CANCEL }
                    disabled={ saving }
                    onClick={ this.onClickCancel }
                />
                { status }
            </div>
        );
    }
}
