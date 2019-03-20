import { h, Component } from 'preact';

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
                    ? [
                        <textarea value={ words.join( '\n' ) } />,
                        <button onClick={ this.onClickSave }>Save</button>,
                        <button onClick={ this.onClickCancel }>Cancel</button>,
                    ]
                    : [
                        words.join( ', ' ),
                        <span className={ S.edit }> 📝</span>,
                    ]
                }
            </dd>
        );
    }
}
