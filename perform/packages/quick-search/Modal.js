import { h, Component }     from 'preact';

import Query                from './Query';
import Results              from './Results';
import S                    from './Modal.sass';


export default class QuickSearchModal extends Component {

    state = {
        query:              '',
    };

    onChangeQuery = query => {
        this.setState({ query });
    };

    onClickBackground = evt => {
        this.props.onClose();
    };

    onClickModal = evt => {
        evt.stopPropagation();
    };

    onKeyDown = evt => {
        if( evt.key === 'Escape' ) {
            this.props.onClose();
        }
    };

    render( props, { query }) {
        return (
            <div
                className={ S.className }
                onClick={ this.onClickBackground }
                onKeyDown={ this.onKeyDown }
                tabindex="0"
            >
                <div className={ S.modal } onClick={ this.onClickModal }>
                    <h1>Search</h1>
                    <Query
                        autofocus
                        onChange={ this.onChangeQuery }
                        value={ query }
                    />
                    <Results
                        onSelect={ this.props.onClose }
                        query={ query }
                    />
                </div>
            </div>
        );
    }
}
