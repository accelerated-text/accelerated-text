import { h, Component }         from 'preact';
import PropTypes                from 'prop-types';

import { composeQueries }       from '../graphql';

import { createDictionaryItem } from '../graphql/mutations.graphql';


export default composeQueries({
    createDictionaryItem,
})( class DictionaryAddItem extends Component {

    static propTypes = {
        refetchQueries: PropTypes.array,
    };

    state = {
        name:           '',
    };

    onChangeName = evt =>
        this.setState({
            name:       evt.target.value,
        });

    onSubmit = evt => {
        evt.preventDefault();

        this.setState({
            name:       '',
        });
        this.props.createDictionaryItem({
            refetchQueries: () =>
                this.props.refetchQueries || [],
            variables: {
                name:   this.state.name,
            },
        });
    };
    
    render() {
        return (
            <form onSubmit={ this.onSubmit }>
                <input
                    onChange={ this.onChangeName }
                    value={ this.state.name }
                />
                <button
                    children="➕ Add"
                    type="submit"
                />
            </form>
        );
    }
});
