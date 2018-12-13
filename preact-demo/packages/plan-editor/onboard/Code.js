import { h, Component } from 'preact';

import OnboardBlocker   from '../../onboard-blocker/OnboardBlocker';
import useStores        from '../../context/use-stores';

import { QA }           from '../qa.constants';

import S                from './Code.sass';


export default useStores([
    'planEditor',
])( class OnboardCode extends Component {

    state = {
        inputValue:     '',
    };

    onChangeInput = e =>
        this.setState({ inputValue: e.target.value });

    onSubmitInput = e => {
        e.preventDefault();

        this.props.planEditor.onSubmitTextExample({
            text:       this.state.inputValue,
        });
    };

    render() {
        const {
            children,
            planEditor: {
                onClickAddExample,
                tokenizerError,
                tokenizerLoading,
                workspaceXml,
            },
        } = this.props;

        return (
            <div className={ S.className }>
                { !workspaceXml &&
                    <div className={ S.options }>
                        { !tokenizerLoading && [
                            <div className={ S.addSegment }>
                                <button
                                    className={ QA.ADD_EXAMPLE }
                                    onClick={ onClickAddExample }
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
                                disabled={ tokenizerLoading }
                                onChange={ this.onChangeInput }
                                placeholder="Input a text example"
                                rows="3"
                                value={ tokenizerLoading ? 'loading...' : this.state.inputValue }
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
                <OnboardBlocker showBlock={ !workspaceXml }>
                    { children }
                </OnboardBlocker>
            </div>
        );
    }
});
