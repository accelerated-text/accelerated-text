import classnames       from 'classnames';
import { h }            from 'preact';

import ClockSpinner     from '../clock-spinner/ClockSpinner';
import { QA }           from '../tests/constants';


export default ({ className, justIcon, message = 'Loading' }) => {
    const text =        message && message.toString();

    return (
        <a
            className={ classnames( className, QA.UI_LOADING ) }
            title={ text }
        >
            <ClockSpinner />
            { justIcon ? null : [ ' ', text ] }
        </a>
    );
};
