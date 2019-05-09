import classnames       from 'classnames';
import { h }            from 'preact';

import { QA }           from '../tests/constants';


export default ({ className, justIcon, message }) => {
    const text =        message && message.toString();

    return (
        <a
            className={ classnames( className, QA.UI_INFO ) }
            title={ text }
        >
            ℹ️
            { justIcon ? null : [ ' ', text ] }
        </a>
    );
};
