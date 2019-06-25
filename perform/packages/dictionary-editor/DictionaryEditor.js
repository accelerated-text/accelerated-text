import { h }                    from 'preact';
import {
    Bar,
    Container,
    Section,
}   from 'react-simple-resizer';

import { composeQueries }       from '../graphql/';

import { closeDictionaryItem }  from '../accelerated-text/local-state';
import { dictionaryItem }       from '../graphql/queries.graphql';
import Thesaurus                from '../thesaurus/Thesaurus';

import Phrases                  from './Phrases';
import S                        from './DictionaryEditor.sass';


export default composeQueries({
    closeDictionaryItem,
    dictionaryItem:             [ dictionaryItem, { id: 'itemId' }],
})(({
    closeDictionaryItem,
    dictionaryItem:             { dictionaryItem: item },
}) =>
    <Container className={ S.className }>
        <Section defaultSize={ 100 }>
            <Thesaurus className={ S.thesaurus } />
        </Section>
        <Bar children="⋮" className={ S.separator } />
        <Section className={ S.main } defaultSize={ 300 } minSize="25%">
            <h2 className={ S.title }>{ item && item.name }</h2>
            <div className={ S.close }>
                <button onClick={ closeDictionaryItem }>
                    ✖️ close
                </button>
            </div>
            { item &&
                <Phrases
                    className={ S.phrases }
                    itemId={ item.id }
                    phrases={ item.phrases }
                />
            }
        </Section>
    </Container>
);
