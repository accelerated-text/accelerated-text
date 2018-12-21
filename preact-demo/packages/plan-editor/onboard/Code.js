import { h, Component } from 'preact';

import OnboardBlocker   from '../../onboard-blocker/OnboardBlocker';
import { useStores }    from '../../vesa/';

import { QA }           from '../qa.constants';

import S                from './Code.sass';


export default useStores([
    'planEditor',
    'tokenizer',
])( class OnboardCode extends Component {

    onChangeInput = e =>
        this.props.E.planEditor.onChangeTextExample(
            e.target.value
        );

    onSubmitInput = e => {
        e.preventDefault();

        this.props.E.planEditor.onSubmitTextExample();
    };

    render() {
        const {
            children,
            E,
            planEditor: {
                textExample,
                workspaceXml,
            },
            tokenizer,
        } = this.props;

        return (
            <div className={ S.className }>
                { !workspaceXml &&
                    <div className={ S.options }>
                        { !tokenizer.loading && [
                            <div className={ S.addSegment }>
                                <button
                                    className={ QA.ADD_EXAMPLE }
                                    onClick={ E.planEditor.onClickAddExample }
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
                <OnboardBlocker showBlock={ !workspaceXml }>
                    { children }
                </OnboardBlocker>
            </div>
        );
    }
});
