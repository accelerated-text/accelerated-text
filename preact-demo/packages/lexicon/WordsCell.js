import { h, Component } from 'preact';

import EditWords        from './EditWords';
import S                from './WordsCell.sass';


export default class WordsCell extends Component {

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

    render({ words }) {
        const {
            isEdited,
        } = this.state;

        return (
            <dd className={ S.className } onClick={ this.onClickCell }>
                { isEdited
                    ? <EditWords
                        words={ words }
                        onClickCancel={ this.onClickCancel }
                        onClickSave={ this.onClickSave }
                    />
                    : [
                        words.join( ', ' ),
                        <span className={ S.edit }> 📝</span>,
                    ]
                }
            </dd>
        );
    }
}
