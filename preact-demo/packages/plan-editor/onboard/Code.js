import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { mount, useStores } from '../../vesa/';
import OnboardBlocker       from '../../onboard-blocker/OnboardBlocker';
import tokenizer            from '../../tokenizer/store';
import tokenizerAdapter     from '../../tokenizer/adapter';

import { QA }               from '../qa.constants';

import onboardCode          from './code-store';
import onboardCodeAdapter   from './code-adapter';
import S                    from './Code.sass';


export default mount({
    onboardCode,
    tokenizer,
}, [
    onboardCodeAdapter,
    tokenizerAdapter,
])( useStores([
    'onboardCode',
    'tokenizer',
])( class OnboardCode extends Component {

    static propTypes = {
        blocklyXml:         PropTypes.any,
    };

    onChangeInput = e =>
        this.props.E.onboardCode.onChangeTextExample(
            e.target.value
        );

    onSubmitInput = e => {
        e.preventDefault();

        this.props.E.tokenizer.onCall(
            this.props.onboardCode.textExample
        );
    };

    render() {
        const {
            blocklyXml,
            children,
            E,
            onboardCode: {
                textExample,
            },
            tokenizer,
        } = this.props;

        return (
            <div className={ S.className }>
                { !blocklyXml &&
                    <div className={ S.options }>
                        { !tokenizer.loading && [
                            <div className={ S.addSegment }>
                                <button
                                    className={ QA.ADD_EXAMPLE }
                                    onClick={ E.onboardCode.onClickAddExample }
                                >
                                    Add
                                </button>
                                {' '}<em>description</em>
                                {' segment with all attributes.'}
                            </div>,
                            <div className={ S.or }>OR</div>,
                        ]}
                        <form className={ S.textForm } onSubmit={ this.onSubmitInput }>
                            <textarea
                                disabled={ tokenizer.loading }
                                onInput={ this.onChangeInput }
                                placeholder="Input a text example"
                                rows="3"
                                value={ tokenizer.loading ? 'loading...' : textExample }
                            />
                            <button
                                children={ tokenizer.loading ? '...' : 'Go' }
                                disabled={ tokenizer.loading }
                                type="submit"
                            />
                            { tokenizer.error &&
                                <div className={ S.tokenizerError }>{
                                    tokenizer.error.toString()
                                }</div>
                            }
                        </form>
                    </div>
                }
                <OnboardBlocker showBlock={ !blocklyXml }>
                    { children }
                </OnboardBlocker>
            </div>
        );
    }
}));
