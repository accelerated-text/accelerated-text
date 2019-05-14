import classnames           from 'classnames';
import { h, Component }     from 'preact';

import { Error, Loading }   from '../ui-messages';
import { QA }               from '../tests/constants';

import S                    from './EditPhrases.sass';


export default class LexiconEditLines extends Component {

    state = {
        text:   this.props.phrases.join( '\n' ),
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

    render({ status }, { text }) {
        return (
            <div className={ classnames( S.className, QA.LEXICON_EDIT ) }>
                <textarea
                    className={ QA.LEXICON_EDIT_TEXT }
                    disabled={ status.saving }
                    onInput={ this.onInput }
                    value={ text }
                />
                <button
                    children="Save"
                    className={ QA.LEXICON_EDIT_SAVE }
                    disabled={ status.saving }
                    onClick={ this.onClickSave }
                />
                <button
                    children="Cancel"
                    className={ QA.LEXICON_EDIT_CANCEL }
                    disabled={ status.saving }
                    onClick={ this.onClickCancel }
                />
                {
                    status.error
                        ? <Error message={ status.error } />
                    : status.saving
                        ? <Loading message="Saving..." />
                    : null
                }
            </div>
        );
    }
}
