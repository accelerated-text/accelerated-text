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

import S                        from './DictionaryEditor.sass';
import UsageModels              from './UsageModels';


export default composeQueries({
    closeDictionaryItem,
    dictionaryItem:             [ dictionaryItem, { id: 'openedPhrase' }],
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
            <h2 className={ S.title }>{ item.name }</h2>
            <div className={ S.close }>
                <button onClick={ closeDictionaryItem }>
                    ✖️ close
                </button>
            </div>
            <UsageModels
                className={ S.usageModels }
                itemId={ item && item.id }
                usageModels={ item && item.usageModels }
            />
        </Section>
    </Container>
);
