import classnames           from 'classnames';
import { h }                from 'preact';

import S                    from './ShowPhrases.sass';


export default ({ className, isEditable, onClick, phrases }) =>
    <div
        className={ classnames(
            S.className,
            isEditable && S.isEditable,
            className,
        ) }
        onClick={ onClick || null }
    >
        { phrases.map( phrase =>
            <span className={ S.phrase }>{ phrase }</span>
        )}
        { isEditable &&
            <span className={ S.editIcon }> 📝</span>
        }
    </div>;
