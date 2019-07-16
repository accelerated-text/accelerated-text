import classnames           from 'classnames';
import { h }                from 'preact';
import { sortBy, prop }     from 'ramda';

import { composeQueries }   from '../graphql/';
import { readerFlags }      from '../graphql/queries.graphql';
import sortFlags            from '../reader-flags/sort';

import AddPhrase            from './AddPhrase';
import Phrase               from './Phrase';
import S                    from './Phrases.sass';


const sortByText =          sortBy( prop( 'text' ));


export default composeQueries({
    readerFlags,
})(({
    className,
    itemId,
    phrases,
    readerFlags: { readerFlags },
}) => {
    const sortedFlags =     sortFlags( readerFlags );

    return (
        <div className={ classnames( S.className, className ) }>
            <div className={ S.inner }>
                <table>
                    <thead>
                        <tr>
                            <th>Phrases</th>
                            <th>Default</th>
                            { sortedFlags.map( flag =>
                                <th key={ flag.id }>{ flag.name }</th>
                            )}
                        </tr>
                    </thead>
                    <tbody>
                        { phrases && sortByText( phrases ).map( phrase =>
                            <Phrase
                                key={ phrase.id }
                                phrase={ phrase }
                                readerFlags={ sortedFlags }
                            />
                        )}
                    </tbody>
                    <AddPhrase
                        className={ S.addPhrase }
                        itemId={ itemId }
                    />
                </table>
            </div>
        </div>
    );
});
