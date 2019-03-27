import { h, Component } from 'preact';

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
            <div className={ S.className }>
                <textarea disabled={ saving } onInput={ this.onInput } value={ text } />
                <button disabled={ saving } onClick={ this.onClickSave }>Save</button>
                <button disabled={ saving } onClick={ this.onClickCancel }>Cancel</button>
                { status }
            </div>
        );
    }
}
