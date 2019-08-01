import { h, Component }     from 'preact';

import { Query }            from '../graphql/';
import { quickSearch }      from '../graphql/queries.graphql';

import Form                 from './Form';
/// import Results              from './Results';


export default class QuickSearch extends Component {

    state = {
        query:              '',
    };

    onChangeQuery = query => {
        this.setState({ query });
    };

    render = ({ onChooseResult }, { query }) =>
        <Query query={ quickSearch } variables={{ query }}>
            { ({
                error,
                data: { quickSearch },
                loading,
            }) =>
                <Form
                    autofocus
                    items={ quickSearch && quickSearch.words }
                    onChangeQuery={ this.onChangeQuery }
                    onChooseResult={ onChooseResult }
                    query={ query }
                />
            }
        </Query>;
}
