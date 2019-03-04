import  classnames          from 'classnames';
import { h }                from 'preact';

import S                    from './Item.sass';


export default ({ children, className, isExpanded, title }) =>
    <div className={ classnames( S.className, className ) }>
        <div className={ S.title }>{ title }</div>
        { isExpanded &&
            <div className={ S.body }>
                { children }
            </div>
        }
    </div>;
