import classnames           from 'classnames';
import { h }                from 'preact';
import { sortBy, prop }     from 'ramda';

import { composeQueries }   from '../graphql/';
import { readerFlags }      from '../graphql/queries.graphql';

import AddPhrase            from './AddPhrase';
import Row                  from './UsageModelRow';
import S                    from './UsageModels.sass';


const sortByPhrase =        sortBy( prop( 'phrase' ));


export default composeQueries({
    readerFlags,
})(({
    className,
    itemId,
    usageModels,
    readerFlags: { readerFlags },
}) =>
    <div className={ classnames( S.className, className ) }>
        <table>
            <thead>
                <tr>
                    <th className={ S.phrases }>Phrases</th>
                    <th>Default</th>
                    { readerFlags && readerFlags.map( flag =>
                        <th key={ flag.id }>{ flag.name }</th>
                    )}
                </tr>
            </thead>
            <tbody>
                { usageModels && sortByPhrase( usageModels ).map( model =>
                    <Row key={ model.id } model={ model } />
                )}
            </tbody>
            <AddPhrase
                className={ S.newPhrase }
                itemId={ itemId }
            />
        </table>
    </div>
);
