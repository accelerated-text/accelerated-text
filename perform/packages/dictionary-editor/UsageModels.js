import classnames           from 'classnames';
import { h }                from 'preact';

import { composeQueries }   from '../graphql/';
import { readerFlags }      from '../graphql/queries.graphql';

import Row                  from './UsageModelRow';
import S                    from './UsageModels.sass';


export default composeQueries({
    readerFlags,
})(({
    className,
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
                { usageModels && usageModels.map( model =>
                    <Row key={ model.id } model={ model } />
                )}
            </tbody>
        </table>
    </div>
);
