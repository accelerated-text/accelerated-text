import { h }            from 'preact';

import ClockSpinner     from '../clock-spinner/ClockSpinner';


export default ({ className, justIcon, message = 'Loading' }) =>
    <a
        className={ className }
        title={ message && message.toString() }
    >
        <ClockSpinner />
        { justIcon ? null : [ ' ', message ] }
    </a>;
