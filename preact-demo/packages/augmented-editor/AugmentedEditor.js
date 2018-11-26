import { h, Component } from 'preact';

import ABlock from '../blocks/ABlock';
import tokenizer from '../tokenizer/tokenizer';

import S from './AugmentedEditor.sass';


export default class AugmentedEditor extends Component {

    state = {
        inputText:          '',
        tokens:             [],
    };

    onChangeInput = e =>
        this.setState({
            inputText:      e.target.value,
        });

    onSubmitInput = async e => {

        e.preventDefault();

        const newTokens =   await tokenizer( this.state.inputText );

        this.setState(
            curState => ({
                inputText:  '',
                tokens:     [ ...curState.tokens, newTokens ],
            }),
            () =>
                this.props.onChangeTokens &&
                    this.props.onChangeTokens( this.state.tokens ),
        );
    };

    render() {
        const { inputText, tokens } = this.state;

        return (
            <div className={ S.className }>
                <div className={ S.code }>
                    { tokens && <ABlock block={ tokens } /> }
                </div>
                <form
                    className={ S.input }
                    onSubmit={ this.onSubmitInput }
                >
                    <input
                        className="qa-augmented-editor-text-input"
                        onChange={ this.onChangeInput }
                        placeholder="Enter your text here"
                        value={ inputText }
                    />
                </form>
            </div>
        );
    }
}
