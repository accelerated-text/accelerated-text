import classnames       from 'classnames';
import { h, Component } from 'preact';

import EditWords        from './EditWords';
import S                from './ItemRow.sass';


export default class LexiconItemRow extends Component {

    state = {
        isEdited:       false,
    };

    onClickCell = () => {

        this.setState({
            isEdited:   true,
        });
    };

    onClickCancel = evt => {

        evt.stopPropagation();
        this.setState({
            isEdited:   false,
        });
    };

    onClickSave = evt => {

        evt.stopPropagation();
        this.setState({
            isEdited:   false,
        });
    };

    render({ className, item }, { isEdited }) {
        return (
            <div className={ classnames( S.className, className ) }>
                <div className={ S.key }>{ item.key }</div>
                <div className={ S.words } onClick={ this.onClickCell }>
                    { isEdited
                        ? <EditWords
                            words={ item.synonyms }
                            onClickCancel={ this.onClickCancel }
                            onClickSave={ this.onClickSave }
                        />
                        : [
                            item.synonyms.join( ', ' ),
                            <span className={ S.edit }> 📝</span>,
                        ]
                    }
                </div>
            </div>
        );
    }
}
