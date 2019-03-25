import { h, Component } from 'preact';

import S                from './EditWords.sass';


export default class LexiconEditWords extends Component {

    onInput = evt =>
        this.props.onChangeText( evt.target.value );

    render({ loading, onClickCancel, onClickSave, status, text }) {
        return (
            <div className={ S.className }>
                <textarea disabled={ loading } onInput={ this.onInput } value={ text } />
                <button disabled={ loading } onClick={ onClickSave }>Save</button>
                <button disabled={ loading } onClick={ onClickCancel }>Cancel</button>
                { status }
            </div>
        );
    }
}
