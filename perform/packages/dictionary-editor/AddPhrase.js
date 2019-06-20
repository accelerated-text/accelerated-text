import classnames                   from 'classnames';
import { h, Component }             from 'preact';

import { composeQueries }           from '../graphql/';
import { createPhraseUsageModel }   from '../graphql/mutations.graphql';
import { QA }                       from '../tests/constants';
import { readerFlags }              from '../graphql/queries.graphql';
import UsageTd                      from '../usage/UsageTd';

import S                            from './AddPhrase.sass';


export default composeQueries({
    createPhraseUsageModel,
    readerFlags,
})( class DictionaryEditorAddPhrase extends Component {

    state = {
        phrase:                     '',
        defaultUsage:               'YES',
    };

    onChangePhrase = evt =>
        this.setState({
            phrase:                 evt.target.value,
        });

    onChangeUsage = defaultUsage =>
        this.setState({ defaultUsage });

    onSubmit = evt => {
        evt.preventDefault();

        const canProceed = (
            this.state.phrase
            && this.props.itemId
        );

        if( !canProceed ) {
            return;
        }
        this.props.createPhraseUsageModel({
            variables: {
                dictionaryItemId:       this.props.itemId,
                phrase:                 this.state.phrase,
                defaultUsage:           this.state.defaultUsage,
            },
        });
        this.setState({
            phrase:                 '',
            defaultUsage:           'YES',
        });
    };
    
    render({
        className,
        readerFlags: { readerFlags },
    }) {
        return (
            <tbody className={ classnames( S.className, className, QA.DICT_ITEM_EDITOR_ADD_PHRASE ) }>
                <tr>
                    <td>
                        <form onSubmit={ this.onSubmit }>
                            <input
                                className={ QA.DICT_ITEM_EDITOR_ADD_PHRASE_TEXT }
                                onChange={ this.onChangePhrase }
                                value={ this.state.phrase }
                            />
                        </form>
                    </td>
                    <UsageTd
                        defaultUsage
                        onChange={ this.onChangeUsage }
                        usage={ this.state.defaultUsage }
                    />
                    <td colspan={ ( readerFlags ? readerFlags.length : 0 ) }>
                        <button
                            children="➕ Add new phrase"
                            className={ QA.DICT_ITEM_EDITOR_ADD_PHRASE_ADD }
                            disabled={ !this.state.phrase }
                            onClick={ this.onSubmit }
                        />
                    </td>
                </tr>
            </tbody>
        );
    }
});
