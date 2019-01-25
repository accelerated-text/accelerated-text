import { h }            from 'preact';


export default ({ message, justIcon }) =>
    <a title={ message }>
        ℹ️  { justIcon ? null : message }
    </a>;
