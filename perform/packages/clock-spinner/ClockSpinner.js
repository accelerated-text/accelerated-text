import classnames       from 'classnames';
import { h }            from 'preact';

import S                from './ClockSpinner.sass';

export default props =>
    <span className={ classnames( S.className, props.className ) } />;
