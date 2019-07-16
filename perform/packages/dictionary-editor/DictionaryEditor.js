import classnames               from 'classnames';
import { h }                    from 'preact';
import {
    Bar,
    Container,
    Section,
}   from 'react-simple-resizer';

import { composeQueries }       from '../graphql/';
import { dictionaryItem }       from '../graphql/queries.graphql';
import { QA }                   from '../tests/constants';
import Thesaurus                from '../thesaurus/Thesaurus';

import DeleteItem               from './DeleteItem';
import Phrases                  from './Phrases';
import S                        from './DictionaryEditor.sass';
import Title                    from './Title';


export default composeQueries({
    dictionaryItem:             [ dictionaryItem, { id: 'itemId' }],
})(({
    closeEditor,
    dictionaryItem:             { dictionaryItem: item },
}) =>
    <Container className={ classnames( S.className, QA.DICT_ITEM_EDITOR ) }>
        <Section defaultSize={ 100 }>
            <Thesaurus className={ S.thesaurus } />
        </Section>
        <Bar className={ S.separator } />
        <Section className={ S.editor } defaultSize={ 300 } minSize="25%">
            <div className={ S.header }>
                <Title className={ S.title } item={ item || {} } />
                <div className={ S.close }>
                    <button
                        children="✖️ close"
                        className={ QA.DICT_ITEM_EDITOR_CLOSE }
                        onClick={ closeEditor }
                    />
                </div>
            </div>
            <div className={ S.main }>
                { item &&
                    <Phrases
                        itemId={ item.id }
                        phrases={ item.phrases }
                    />
                }
                <DeleteItem
                    className={ S.deleteItem }
                    itemId={ item.id }
                    onDelete={ closeEditor }
                />
            </div>
        </Section>
    </Container>
);
