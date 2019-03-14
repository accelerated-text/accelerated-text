import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { mount, useStores } from '../vesa/';
import OnboardBlocker       from '../onboard-blocker/OnboardBlocker';
import { QA }               from '../tests/constants';
import tokenizer            from '../tokenizer/store';
import tokenizerAdapter     from '../tokenizer/adapter';

import onboardCode          from './store';
import onboardCodeAdapter   from './adapter';
import S                    from './OnboardCode.sass';


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
        hasCode:            PropTypes.boolean,
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
            children,
            hasCode,
            E,
            onboardCode: {
                textExample,
            },
            tokenizer,
        } = this.props;

        return (
            <div className={ S.className }>
                { !hasCode &&
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
                                {' segment with all cell values.'}
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
                <OnboardBlocker showBlock={ !hasCode }>
                    { children }
                </OnboardBlocker>
            </div>
        );
    }
}));
