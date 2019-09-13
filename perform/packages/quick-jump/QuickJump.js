import {
    h,
    Component,
    createRef,
}                           from 'preact';
import PropTypes            from 'prop-types';
import shortkey             from 'shortkey';

import {
    getAllBlocksWithDistances,
    getBlockText,
}                           from '../blockly-helpers/';

import S                    from './QuickJump.sass';


const getResults = ( workspace, selected, query ) => {
    const regexp =      new RegExp( query, 'i' );

    return getAllBlocksWithDistances( selected, workspace )
        .map( result => ({
            ...result,
            text:       getBlockText( result.block ),
        }))
        .filter(({ text }) => text.match( regexp ))
        .sort(( a, b ) => a.distance - b.distance );
};


export default class QuickJump extends Component {

    static propTypes = {
        Blockly:            PropTypes.object.isRequired,
        onDone:             PropTypes.func,
        selected:           PropTypes.object,
        workspace:          PropTypes.object.isRequired,
    };

    inputRef =              createRef();

    state = {
        offset:             0,
        query:              '',
        results:            null,
    };

    selectResult = () => {
        const {
            offset,
            results,
        } = this.state;
        if( results && results.length ) {
            ( results[offset] || results[0]).block.select();
        } else {
            this.props.selected && this.props.selected.select();
        }
        this.inputRef.current.focus();
    };

    onInput = evt => {
        const query =       evt.target.value;
        const offset =      query === this.state.query ? this.state.offset : 0;

        this.setState({
            offset,
            query,
            results:        getResults( this.props.workspace, this.props.selected, query ),
        }, this.selectResult );
    }

    onClickNext = () =>
        this.setState( state => ({
            offset:     ( state.offset + 1 ) % state.results.length,
        }), this.selectResult );

    onClickPrevious = () =>
        this.setState( state => ({
            offset:     ( state.offset + 1 ) % state.results.length,
        }), this.selectResult );

    onKeyDown = shortkey({
        onEnter: evt => {
            this.props.onDone && this.props.onDone();
        },
        onEscape: evt => {
            this.props.selected && this.props.selected.select();
        },
        onF3: evt => {
            evt.preventDefault();
            this.onClickNext();
        },
        onShiftF3: evt => {
            evt.preventDefault();
            this.onClickPrevious();
        },
    });

    componentDidMount() {
        this.inputRef.current.focus();
    }

    render( _, { offset, query, results }) {
        const resultCount = results ? results.length : 0;
        return (
            <div className={ S.className }>
                <input
                    onInput={ this.onInput }
                    onKeyDown={ this.onKeyDown }
                    placeholder="type to search blocks"
                    ref={ this.inputRef }
                    type="text"
                    value={ query }
                />
                { !! resultCount && [
                    <button onClick={ this.onClickPrevious }>↑ Shift+F3</button>,
                    <button onClick={ this.onClickNext }>F3 ↓</button>,
                ]}
                { !! query &&
                    <span
                        children={ `${ resultCount ? ( offset + 1 ) : 0 } of ${ resultCount } results` }
                        className={ S.hint }
                    />
                }
            </div>
        );
    }
}
