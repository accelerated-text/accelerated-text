import { h, Component }     from 'preact';

import { composeQueries }   from '../graphql/';
import {
    Error,
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
                : sortFlags( readerFlags ).map( flag =>
                    <FlagValue
                        key={ flag.id }
                        flag={ flag }
                        isChecked={ flagValues[flag.id] }
                        onChange={ onToggleFlag }
                    />
                )
            }</div>
        );
    }
});
