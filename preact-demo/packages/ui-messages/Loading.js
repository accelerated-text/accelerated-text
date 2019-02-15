import { h }            from 'preact';

import ClockSpinner     from '../clock-spinner/ClockSpinner';


export default ({ className, justIcon, message = 'Loading' }) => {
    const text =        message && message.toString();

    return (
        <a
            className={ className }
            title={ text }
        >
            <ClockSpinner />
            { justIcon ? null : [ ' ', text ] }
        </a>
    );
};
