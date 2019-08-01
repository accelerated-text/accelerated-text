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
}   from '../ui-messages/';

import Item                 from './Item';
import S                    from './Form.sass';


export default class QuickSearchQuery extends Component {

    inputRef =              createRef();

    state = {
        activeItem:         0,
        itemSubtype:        0,
    };

    onInput = evt => {
        this.props.onChangeQuery( evt.target.value );
    };

    onKeyDown = shortkey({
        onArrowDown: () => {
            this.setState(({ activeItem }) => ({
                activeItem:     ( activeItem + 1 ) % this.props.items.length,
                itemSubtype:    0,
            }));
        },
        onArrowUp: () => {
            this.setState(({ activeItem }) => ({
                activeItem:     activeItem ? activeItem - 1 : 0,
                itemSubtype:    0,
            }));
        },
        onArrowLeft: () => {
            this.setState(({ itemSubtype }) => ({
                itemSubtype:    itemSubtype - 1,
            }));
        },
        onArrowRight: () => {
            this.setState(({ itemSubtype }) => ({
                itemSubtype:    itemSubtype + 1,
            }));
        },
        onEnter: evt => {
            evt.preventDefault();
            this.props.onChooseResult(
                this.props.items[ this.state.activeItem ]
            );
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

    componentWillReceiveProps({ items }) {
        if( items !== this.props.items ) {
            this.setState({
                activeItem:     0,
                itemSubtype:    0,
            });
        }
    }

    render(
        { error, items, loading, onChooseResult, query },
        { activeItem, itemSubtype }
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
                { error
                    ? <Error message={ error } />
                : loading
                    ? <Loading />
                : items
                    ? <div className={ S.results }>{
                        items.map(( item, i ) =>
                            <Item
                                isActive={ i === activeItem }
                                item={ item }
                                subtype={ itemSubtype }
                                key={ item.id }
                                onSelect={ onChooseResult }
                            />
                        )
                    }</div>
                    : <Info message="no results found" />
                }
            </div>
        );
    }
}
