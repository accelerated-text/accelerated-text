import { h, Component } from 'preact';

import OnboardBlocker   from '../onboard-blocker/OnboardBlocker';
import useStores        from '../context/use-stores';

import { QA }           from './qa.constants';
import S                from './OnboardCode.sass';


export default useStores([
    'generatorEditor',
])( class OnboardCode extends Component {

    state = {
        inputValue:     '',
    };

    onChangeInput = e =>
        this.setState({ inputValue: e.target.value });

    onSubmitInput = e => {
        e.preventDefault();

        this.props.generatorEditor.onSubmitTextExample({
            text:       this.state.inputValue,
        });
    };

    render() {
        const {
            children,
            generatorEditor: {
                blocks,
                onClickAddOnboardSegment,
                tokenizerLoading,
                tokenizerError,
            },
        } = this.props;

        return (
            <div className={ S.className }>
                { !blocks &&
                    <div className={ S.options }>
                        <div className={ S.addSegment }>
                            <button
                                className={ QA.ADD_EXAMPLE }
                                onClick={ onClickAddOnboardSegment }
                            >
                                Add
                            </button>
                            {' '}<em>description</em>
                            {' segment with all attributes.'}
                        </div>
                        <div className={ S.or }>OR</div>
                        <form className={ S.textForm } onSubmit={ this.onSubmitInput }>
                            <textarea
                                placeholder="Input a text example"
                                onChange={ this.onChangeInput }
                                rows="3"
                                value={ this.inputValue }
                            />
                            <button
                                children={ tokenizerLoading ? '...' : 'Go' }
                                disabled={ tokenizerLoading }
                                type="submit"
                            />
                            { tokenizerError &&
                                <div className={ S.tokenizerError }>{
                                    tokenizerError.toString()
                                }</div>
                            }
                        </form>
                    </div>
                }
                <OnboardBlocker showBlock={ !blocks }>
                    { children }
                </OnboardBlocker>
            </div>
        );
    }
});
