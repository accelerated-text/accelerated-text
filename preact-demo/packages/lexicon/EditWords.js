import { h, Component } from 'preact';

import S                from './EditWords.sass';


export default class LexiconEditWords extends Component {

    onInput = evt =>
        this.props.onChangeText( evt.target.value );

    render({ onClickCancel, onClickSave, saving, status, text }) {
        return (
            <div className={ S.className }>
                <textarea disabled={ saving } onInput={ this.onInput } value={ text } />
                <button disabled={ saving } onClick={ onClickSave }>Save</button>
                <button disabled={ saving } onClick={ onClickCancel }>Cancel</button>
                { status }
            </div>
        );
    }
}
