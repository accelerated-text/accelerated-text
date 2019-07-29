import { h, Component }     from 'preact';

import S                    from './Modal.sass';


export default class QuickSearchModal extends Component {

    onClickBackground = evt =>
        this.props.onClose();

    onClickModal = evt => {
        evt.stopPropagation();
    };
    
    render() {
        return (
            <div className={ S.className } onClick={ this.onClickBackground }>
                <div className={ S.modal } onClick={ this.onClickModal }>
                    <h1>Search</h1>
                    <form>
                        <input type="search" />
                    </form>
                    <div className={ S.results }>
                        ...results
                    </div>
                </div>
            </div>
        );
    }
}
