import classnames               from 'classnames';
import { h }                    from 'preact';
import {
    Bar,
    Container,
    Section,
}   from 'react-simple-resizer';

import { composeQueries }       from '../graphql/';

import { closeDictionaryItem }  from '../accelerated-text/local-state';
import { dictionaryItem }       from '../graphql/queries.graphql';
import { QA }                   from '../tests/constants';
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
    <Container className={ classnames( S.className, QA.DICT_ITEM_EDITOR ) }>
        <Section defaultSize={ 100 }>
            <Thesaurus className={ S.thesaurus } />
        </Section>
        <Bar children="⋮" className={ S.separator } />
        <Section className={ S.main } defaultSize={ 300 } minSize="25%">
            <h2 className={ classnames( S.title, QA.DICT_ITEM_EDITOR_NAME ) }>
                { item && item.name }
            </h2>
            <div className={ S.close }>
                <button
                    children="✖️ close"
                    className={ QA.DICT_ITEM_EDITOR_CLOSE }
                    onClick={ closeDictionaryItem }
                />
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
