import { h, Component } from 'preact';

import S                from './EditWords.sass';


export default class EditWords extends Component {

    state = {
        text:           this.props.words.join( '\n' ),
    };

    onInput = evt =>
        this.setState({ text: evt.target.value });

    render( _, { text }) {
        return (
            <div className={ S.className }>
                <textarea value={ text } onInput={ this.onInput } />
                <button onClick={ this.props.onClickSave }>Save</button>
                <button onClick={ this.props.onClickCancel }>Cancel</button>
            </div>
        );
    }
}
