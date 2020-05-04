import classnames               from 'classnames';
import { h, Component }         from 'preact';

import DragInBlock              from '../drag-in-blocks/DragInBlock';
import DictionaryItemBlock      from '../nlg-blocks/Dictionary-item';
import DictionaryItemModifier   from '../nlg-blocks/Dictionary-item-modifier';
import { QA }                   from '../tests/constants';
import SmallPOS                 from '../part-of-speech/SmallView';
import UIContext                from '../accelerated-text/UIContext';

import S                        from './ItemRow.sass';
import ShowPhrases              from './ShowPhrases';


export default class DictionaryItemRow extends Component {

    static contextType =        UIContext;

    openItem = () =>
        this.context.openDictionaryItem( this.props.item.id );

    render = ({ item }) =>
        <tr className={ classnames( S.className, QA.DICTIONARY_ITEM ) }>
            <td className={ S.dragInBlock }>
                { item &&
                    <DragInBlock
                        block={ DictionaryItemBlock }
                        mutation={{
                            id:           item.id,
                            name:         item.name,
                            pos:          item.partOfSpeech,
                        }}
                    />
                }
            </td>
            <td className={ S.dragInBlock }>
                { item &&
                    <DragInBlock
                        block={ DictionaryItemModifier }
                        mutation={{
                            id:     item.id,
                            name:   item.name,
                            pos:    item.partOfSpeech,
                        }}
                    />
                }
            </td>
            <td
                className={ classnames( S.name, QA.DICTIONARY_ITEM_NAME ) }
                onClick={ this.openItem }
            >
                { item && [
                    item.name,
                    <SmallPOS
                        className={ S.pos }
                        partOfSpeech={ item.partOfSpeech }
                    />,
                ]}
            </td>
            <td className={ QA.DICTIONARY_ITEM_PHRASES } onClick={ this.openItem }>
                { item &&
                    <ShowPhrases
                        phrases={
                            item.phrases
                                .map( phrase => phrase.text )
                                .sort()
                        }
                    />
                }
            </td>
        </tr>;
}
