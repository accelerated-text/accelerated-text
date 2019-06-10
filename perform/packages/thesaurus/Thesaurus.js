import classnames               from 'classnames';
import { h, Component }         from 'preact';

import S                        from './Thesaurus.sass';
import Search                   from './Search';
import Synonyms                 from './Synonyms';


export default class Thesaurus extends Component {

    state = {
        phrase:                 null,
        query:                  '',
    };

    onChangeQuery = evt =>
        this.setState({
            query:              evt.target.value,
        });

    onClickBack = () =>
        this.setState({
            phrase:             null,
        });
    
    onClickPhrase = phrase =>
        this.setState({ phrase });

    render({ className }, { phrase, query }) {
        return (

            <div className={ classnames( S.className, className ) }>
                <h3 className={ S.title }>Thesaurus</h3>
                { phrase
                    ? <Synonyms
                        onClickBack={ this.onClickBack }
                        onClickPhrase={ this.onClickPhrase }
                        phrase={ phrase }
                    />
                    : <Search
                        onChangeQuery={ this.onChangeQuery }
                        onClickPhrase={ this.onClickPhrase }
                        query={ query }
                    />
                }
            </div>
        );
    }
}
