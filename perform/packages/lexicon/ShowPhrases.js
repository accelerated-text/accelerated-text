import classnames           from 'classnames';
import { h }                from 'preact';

import S                    from './ShowPhrases.sass';


export default ({ className, onClick, phrases }) =>
    <div
        className={ classnames( S.className, className ) }
        onClick={ onClick }
    >
        { phrases.map( phrase =>
            <span className={ S.phrase }>{ phrase }</span>
        )}
        <span className={ S.editIcon }> 📝</span>
    </div>;
