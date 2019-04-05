import  classnames          from 'classnames';
import { h }                from 'preact';

import S                    from './Sidebar.sass';


export default ({ children, className }) =>
    <div className={ classnames( S.className, className ) }>
        { children }
    </div>;
