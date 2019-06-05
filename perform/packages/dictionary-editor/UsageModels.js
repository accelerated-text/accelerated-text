import classnames           from 'classnames';
import { h }                from 'preact';

import { composeQueries }   from '../graphql/';
import { readerFlags }      from '../graphql/queries.graphql';
import UsageTd              from '../usage/UsageTd';

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
                    { readerFlags && readerFlags.map(
                        flag => <th>{ flag.name }</th>
                    )}
                </tr>
            </thead>
            <tbody>
                { usageModels && usageModels.map(
                    model =>
                        <tr key={ model.phrase.id }>
                            <td>{ model.phrase.text }</td>
                            <UsageTd usage={ model.defaultUsage.usage } />
                            { model.readerUsage.map(
                                flagUsage => <UsageTd usage={ flagUsage.usage } />
                            )}
                        </tr>
                )}
            </tbody>
        </table>
    </div>
);
