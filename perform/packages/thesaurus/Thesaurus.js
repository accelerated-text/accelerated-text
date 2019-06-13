import classnames               from 'classnames';
import { h, Component }         from 'preact';

import S                        from './Thesaurus.sass';
import Search                   from './Search';
import Synonyms                 from './Synonyms';


export default class Thesaurus extends Component {

    state = {
        word:                   null,
        query:                  '',
    };

    onChangeQuery = evt =>
        this.setState({
            query:              evt.target.value,
        });

    onClickBack = () =>
        this.setState({
            word:               null,
        });
    
    onClickWord = word =>
        this.setState({ word });

    render({ className }, { word, query }) {
        return (

            <div className={ classnames( S.className, className ) }>
                <h3 className={ S.title }>Thesaurus</h3>
                { word
                    ? <Synonyms
                        onClickBack={ this.onClickBack }
                        onClickWord={ this.onClickWord }
                        word={ word }
                    />
                    : <Search
                        onChangeQuery={ this.onChangeQuery }
                        onClickWord={ this.onClickWord }
                        query={ query }
                    />
                }
            </div>
        );
    }
}
