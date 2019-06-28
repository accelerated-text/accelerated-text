import classnames               from 'classnames';
import { h, Component }         from 'preact';

import { composeQueries }       from '../graphql/';
import { QA }                   from '../tests/constants';
import { updateDictionaryItem } from '../graphql/mutations.graphql';

import S                        from './Title.sass';


export default composeQueries({
    updateDictionaryItem,
})( class DictionaryEditorTitle extends Component {

    state = {
        isEditing:              false,
        name:                   '',
    };

    onClickCancel = () =>
        this.setState({
            isEditing:          false,
        });

    /// TODO: focus input after this
    onClickTitle = () =>
        this.setState({
            isEditing:          true,
            name:               this.props.item.name,
        });

    /// TODO: close editor if [Esc] pressed
    onInput = evt =>
        this.setState({
            name:               evt.target.value,
        });

    onSubmit = () => {
        this.setState({
            isEditing:          false,
        });
        this.props.updateDictionaryItem({
            optimisticResponse: {
                __typename:     'Mutation',
                updateDictionaryItem: {
                    ...this.props.item,
                    name:       this.state.name,
                },
            },
            variables: {
                id:             this.props.item.id,
                name:           this.state.name,
            },
        });
    };

    render({ className, item }, { isEditing, name }) {
        return (
            <div className={ classnames( S.className, className ) }>{
                isEditing
                    ? <form onSubmit={ this.onSubmit }>
                        <input value={ name } onInput={ this.onInput } />
                        <button children="✔️ Save" type="submit" />
                        <button children="✖️ Cancel" type="reset" onClick={ this.onClickCancel } />
                    </form>
                    : <h2
                        children={ item.name }
                        className={ classnames( QA.DICT_ITEM_EDITOR_NAME ) }
                        onClick={ this.onClickTitle }
                    />
            }</div>
        );
    }
});

