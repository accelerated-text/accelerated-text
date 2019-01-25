import { h }            from 'preact';


export default ({ message, justIcon }) =>
    <a title={ message }>
        🛑 { justIcon ? null : message }
    </a>;
