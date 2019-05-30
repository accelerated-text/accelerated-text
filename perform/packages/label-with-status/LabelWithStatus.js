import { h }                from 'preact';

import { Error, Loading }   from '../ui-messages/';

import S                    from './LabelWithStatus.sass';


export default ({ error, loading, label }) =>
    <div className={ S.className }>
        <span className={ S.label }>{ label }</span>
        <span className={ S.status }>
            { error && <Error justIcon message={ error } /> }
            { loading && <Loading /> }
        </span>
    </div>
