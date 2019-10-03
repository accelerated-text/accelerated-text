import {
    h,
    Component,
    createRef,
}                           from 'preact';
import shortkey             from 'shortkey';

import {
    Error,
    Info,
    Loading,
}                           from '../ui-messages/';

import Result               from './Result';
import S                    from './Form.sass';


const FAST_JUMP =           6;


export default class QuickSearchForm extends Component {

    inputRef =              createRef();

    state = {
        activeResult:       0,
        selectedType:       0,
    };

    onInput = evt => {
        this.props.onChangeQuery( evt.target.value );
    };

    onKeyDown = shortkey({
        onArrowDown: () => {
            this.setState(({ activeResult }) => ({
                activeResult:   ( activeResult + 1 ) % this.props.results.length,
                selectedType:   0,
            }));
        },
        onArrowUp: () => {
            this.setState(({ activeResult }) => ({
                activeResult:   activeResult ? activeResult - 1 : 0,
                selectedType:   0,
            }));
        },
        onArrowLeft: () => {
            this.setState(({ selectedType }) => ({
                selectedType:   selectedType - 1,
            }));
        },
        onArrowRight: () => {
            this.setState(({ selectedType }) => ({
                selectedType:   selectedType + 1,
            }));
        },
        onPageDown: () => {
            this.setState(({ activeResult }) => ({
                activeResult: Math.min(
                    this.props.results.length - 1,
                    activeResult + FAST_JUMP,
                ),
            }));
        },
        onPageUp: () => {
            this.setState(({ activeResult }) => ({
                activeResult: Math.max(
                    0,
                    activeResult - FAST_JUMP,
                ),
            }));
        },
        onEnter: evt => {
            evt.preventDefault();
            const {
                onChooseResult,
                results,
            } = this.props;
            if( results && results.length ) {
                onChooseResult({
                    ...results[ this.state.activeResult ],
                    selectedType:   this.state.selectedType,
                });
            }
        },
        onTab: evt => {
            evt.preventDefault();
        },
    });

    componentDidMount() {
        if( this.props.autofocus ) {
            this.inputRef.current.focus();
        }
    }

    componentWillReceiveProps({ results }) {
        if( results !== this.props.results ) {
            this.setState({
                activeResult:   0,
                selectedType:   0,
            });
        }
    }

    render(
        { error, results, loading, onChooseResult, query },
        { activeResult, selectedType }
    ) {
        return (
            <div
                className={ S.className }
                onKeyDown={ this.onKeyDown }
                tabIndex="0"
            >
                <input
                    onInput={ this.onInput }
                    placeholder="Search blocks"
                    ref={ this.inputRef }
                    type="search"
                    value={ query }
                />
                { error && <Error message={ error } /> }
                <div className={ S.results }>{
                    results.map(( result, i ) =>
                        <Result
                            isActive={ i === activeResult }
                            result={ result }
                            selectedType={ ( i === activeResult ) ? selectedType : 0 }
                            key={ result.id }
                            onChoose={ onChooseResult }
                        />
                    )
                }</div>
                { loading && <Loading /> }
                { ! results.length &&
                    <Info message="No suitable blocks found for your search." />
                }
            </div>
        );
    }
}
