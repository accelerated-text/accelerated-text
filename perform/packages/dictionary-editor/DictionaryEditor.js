import { h }                    from 'preact';

import { composeQueries }       from '../graphql/';

import { closeDictionaryItem }  from '../accelerated-text/graphql';
import { dictionaryItem }       from '../graphql/queries.graphql';

import S                        from './DictionaryEditor.sass';
import UsageModels              from './UsageModels';


export default composeQueries({
    closeDictionaryItem,
    dictionaryItem:          [ dictionaryItem, { id: 'openedPhrase' }],
})(({
    closeDictionaryItem,
    dictionaryItem: { dictionaryItem: item },
}) =>
    <div className={ S.className }>
        <div className={ S.synonymSearch } />
        <div className={ S.main }>
            <h2 className={ S.title }>{ item.name }</h2>
            <div className={ S.close }>
                <button onClick={ closeDictionaryItem }>
                    ✖️ close
                </button>
            </div>
            <UsageModels
                className={ S.usageModels }
                usageModels={ item && item.usageModels }
            />
        </div>
    </div>
);
