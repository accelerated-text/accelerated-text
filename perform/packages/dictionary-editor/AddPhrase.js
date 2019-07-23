import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';
import { createPhrase }     from '../graphql/mutations.graphql';
import { Error, Loading }   from '../ui-messages/';
import { QA }               from '../tests/constants';
import { readerFlags }      from '../graphql/queries.graphql';
import UsageTd              from '../usage/UsageTd';

import S                    from './AddPhrase.sass';


export default composeQueries({
    createPhrase,
    readerFlags,
})( class DictionaryEditorAddPhrase extends Component {

    static propTypes = {
        itemId:             PropTypes.string.isRequired,
        createPhrase:       PropTypes.func.isRequired,
        readerFlags:        PropTypes.object,
    };

    state = {
        createError:        null,
        createLoading:      false,
        defaultUsage:       'YES',
        text:               '',
    };

    onChangeText = evt =>
        this.setState({
            text:           evt.target.value,
        });

    onChangeDefaultUsage = defaultUsage =>
        this.setState({ defaultUsage });

    onSubmit = evt => {
        evt.preventDefault();

        const canProceed = (
            this.state.text
            && this.props.itemId
            && !this.state.createLoading
        );

        if( !canProceed ) {
            return;
        }

        this.setState({
            createLoading:  true,
            defaultUsage:   'YES',
            text:           '',
        });

        this.props.createPhrase({
            variables: {
                dictionaryItemId:   this.props.itemId,
                text:               this.state.text,
                defaultUsage:       this.state.defaultUsage,
            },
        }).then( mutationResult =>
            this.setState({
                createError:        mutationResult.error,
                createLoading:      false,
            })
        );
    };
    
    render({
        className,
        readerFlags: { readerFlags },
    }, {
        createError,
        createLoading,
        defaultUsage,
        text,
    }) {
        return (
            <tbody className={ classnames( S.className, className, QA.DICT_ITEM_EDITOR_ADD_PHRASE ) }>
                <tr>
                    <td>
                        <form onSubmit={ this.onSubmit }>
                            <input
                                className={ QA.DICT_ITEM_EDITOR_ADD_PHRASE_TEXT }
                                disabled={ createLoading }
                                onChange={ this.onChangeText }
                                value={ text }
                            />
                        </form>
                    </td>
                    <UsageTd
                        defaultUsage
                        onChange={ this.onChangeDefaultUsage }
                        usage={ defaultUsage }
                    />
                    <td colspan={ ( readerFlags ? readerFlags.flags.length : 0 ) }>
                        <button
                            children="➕ Add new phrase"
                            className={ QA.DICT_ITEM_EDITOR_ADD_PHRASE_ADD }
                            disabled={ !text || createLoading }
                            onClick={ this.onSubmit }
                        />
                        { createLoading
                            && <p><Loading message="Saving phrase..." /></p>
                        }
                        { createError
                            && <p><Error message={ createError } /></p>
                        }
                    </td>
                </tr>
            </tbody>
        );
    }
});
