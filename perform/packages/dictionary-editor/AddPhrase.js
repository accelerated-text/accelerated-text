import classnames           from 'classnames';
import { h, Component }     from 'preact';
import PropTypes            from 'prop-types';

import { composeQueries }   from '../graphql/';
import { createPhrase }     from '../graphql/mutations.graphql';
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
        text:               '',
        defaultUsage:       'YES',
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
        );

        if( !canProceed ) {
            return;
        }
        this.props.createPhrase({
            variables: {
                dictionaryItemId:   this.props.itemId,
                text:               this.state.text,
                defaultUsage:       this.state.defaultUsage,
            },
        });
        this.setState({
            text:           '',
            defaultUsage:   'YES',
        });
    };
    
    render({
        className,
        readerFlags: { readerFlags },
    }) {
        return (
            <tbody className={ classnames( S.className, className ) }>
                <tr>
                    <td>
                        <form onSubmit={ this.onSubmit }>
                            <input
                                onChange={ this.onChangeText }
                                value={ this.state.text }
                            />
                        </form>
                    </td>
                    <UsageTd
                        defaultUsage
                        onChange={ this.onChangeDefaultUsage }
                        usage={ this.state.defaultUsage }
                    />
                    <td colspan={ ( readerFlags ? readerFlags.length : 0 ) }>
                        <button
                            children="➕ Add new phrase"
                            disabled={ !this.state.text }
                            onClick={ this.onSubmit }
                        />
                    </td>
                </tr>
            </tbody>
        );
    }
});
