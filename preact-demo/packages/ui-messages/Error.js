import { h }            from 'preact';


export default ({ className, justIcon, message = 'Unknown error' }) =>
    <a
        className={ className }
        title={ message && message.toString() }
    >
        🛑
        { justIcon ? null : [ ' ', message ] }
    </a>;
