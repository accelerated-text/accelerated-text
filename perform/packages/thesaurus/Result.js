import classnames               from 'classnames';
import { h, Component }         from 'preact';

import { composeQueries }       from '../graphql';
import { searchSynonyms }       from '../graphql/queries.graphql';

import S                        from './Result.sass';


export default composeQueries({
    searchSynonyms:       [ searchSynonyms, { phraseId: 'id' }],
})( class ThesaurusSearchResult extends Component {

    state = {
        isExpanded:             false,
    };

    onClick = () =>
        this.setState({
            isExpanded:         !this.state.isExpanded,
        });
    
    render({
        className,
        searchSynonyms: { searchSynonyms },
        text,
    }) {
        return (
            <li
                className={ classnames( S.className, className ) }
                onClick={ this.onClick }
            >
                { text }
                { this.state.isExpanded && searchSynonyms &&
                    <ol className={ S.synonyms }>
                        { searchSynonyms.phrases.map(
                            ({ id, text }) => <li key={ id }>{ text }</li>
                        )}
                    </ol>
                }
            </li>
        );
    }
});

