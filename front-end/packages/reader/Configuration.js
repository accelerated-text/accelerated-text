import { h, Component }     from 'preact';

import { composeQueries }   from '../graphql/';
import {
    Error,
    Info,
    Loading,
}   from '../ui-messages/';
import { readerFlags }      from '../graphql/queries.graphql';
import sortFlags            from '../reader-flags/sort';

import FlagValue            from './FlagValue';
import ReaderContext        from './Context';
import S                    from './Configuration.sass';

export default composeQueries({
    readerFlags,
})( class ReaderConfiguration extends Component {

    static contextType =    ReaderContext;

    render({
        readerFlags: {
            error,
            loading,
            readerFlags,
        },
    }, _, {
        flagValues,
        onToggleFlag,
    }) {
        return (
            <div className={ S.className }>{
                error
                    ? <Error message={ error } />
                : loading
                    ? <Loading />
                : (readerFlags && Object.keys(readerFlags.flags).length > 0)
                  ? sortFlags( readerFlags )
                       .map(flag => {
                         if(!(flag.id in flagValues) && flag.defaultUsage == "YES"){
                           flagValues[flag.id] = true;
                         }
                         return flag;
                       })
                       .map( flag =>
                        <FlagValue
                            key={ flag.id }
                            flag={ flag }
                            isChecked={ flagValues[flag.id] }
                            onChange={ onToggleFlag }
                        />
                        )
                    : <Info message="No reader flags found." />
            }</div>
        );
    }
});
