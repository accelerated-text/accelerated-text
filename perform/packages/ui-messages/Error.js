import classnames       from 'classnames';
import { h }            from 'preact';

import { QA }           from '../tests/constants';


export default ({ className, justIcon, message = 'Unknown error' }) => {
    const text =        message && message.toString();

    return (
        <a
            className={ classnames( className, QA.UI_ERROR ) }
            title={ text }
        >
            🛑
            { justIcon ? null : [ ' ', text ] }
        </a>
    );
};
