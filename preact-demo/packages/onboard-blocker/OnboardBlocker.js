import { h }            from 'preact';

import S                from './OnboardBlocker.sass';


export default ({ children, showBlock }) =>
    <div className={ S.className }>
        { children }
        { showBlock && <div className={ S.block } /> }
    </div>;
