import { h }            from 'preact';

import ClockSpinner     from '../clock-spinner/ClockSpinner';


export default ({ message, justIcon }) =>
    <a title={ message }>
        <ClockSpinner />
        { justIcon ? null : ` ${ message }` }
    </a>;
