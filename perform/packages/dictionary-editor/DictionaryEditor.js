import { h }                    from 'preact';

import { composeQueries }       from '../graphql/';

import { closeDictionaryItem }  from '../accelerated-text/graphql';
import {
    orgDictionaryItem,
    readerFlags,
}   from '../graphql/queries.graphql';

import S                        from './DictionaryEditor.sass';


export default composeQueries({
    closeDictionaryItem,
    orgDictionaryItem:          [ orgDictionaryItem, { id: 'openedPhrase' }],
    readerFlags,
})(({
    closeDictionaryItem,
    orgDictionaryItem: { orgDictionaryItem: item },
    readerFlags: { readerFlags },
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
            <div className={ S.usageModels }>
                <table>
                    <thead>
                        <tr>
                            <th>Phrases</th>
                            <th>Default</th>
                            { readerFlags && readerFlags.map(
                                flag => <th>{ flag.name }</th>
                            )}
                        </tr>
                    </thead>
                    <tbody>
                        { item && item.usageModels.map(
                            usageModel =>
                                <tr key={ usageModel.phrase.id }>
                                    <td>{ usageModel.phrase.text }</td>
                                    <td>{ usageModel.defaultUsage.usage }</td>
                                    { usageModel.readerUsage.map(
                                        flagUsage => <td>{ flagUsage.usage }</td>
                                    )}
                                </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    </div>
);
